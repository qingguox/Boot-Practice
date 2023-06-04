package com.xlg.component.ks.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.AbstractIterator;

/**
 * 提供一个分库分表的迭代器, 解决单表过热问题。
 * <p>
 * 比如有1000张表, 每次找1000条数据, 直到找不到数据为止.
 * 注意:
 * 1. 查询中 where id > minId(cursor) order by id limit :batchSize
 * 如果id是有序的话, 则不用order byd id
 * 2. 查询id或者字段需要有序
 *
 * @param <S> shardKey
 * @param <I> initId
 * @param <E> dataModel
 */
public class BatchShardCursorIterator<S, I, E> implements Iterable<List<E>> {

    private final GetByShardCursorDAO<S, I, E> dao;
    private final I initCursor;
    private final IntSupplier batchSize;
    private final List<S> shardKeyList;
    private final Function<E, I> entityToIdFunction;
    private final Predicate<E> dataFilterPredicate;

    public BatchShardCursorIterator(GetByShardCursorDAO<S, I, E> dao, I initCursor, IntSupplier batchSize,
            List<S> shardKeyList, Function<E, I> entityToIdFunction, Predicate<E> dataFilterPredicate) {
        this.dao = dao;
        this.initCursor = initCursor;
        this.batchSize = batchSize;
        this.shardKeyList = shardKeyList;
        this.entityToIdFunction = entityToIdFunction;
        this.dataFilterPredicate = dataFilterPredicate;
    }

    @NotNull
    @Override
    public Iterator<List<E>> iterator() {
        return new ShardCursorIterator();
    }


    private class ShardCursorIterator extends AbstractIterator<List<E>> {

        private final List<ShardCursor> shardCursorList = buildShardCursorList();
        private final AtomicLong shardCursor = new AtomicLong(0L);


        @Override
        protected List<E> computeNext() {
            int limitSize = batchSize.getAsInt();
            List<E> page = shardCursorGet(limitSize);
            return page.isEmpty() ? endOfData() : page;
        }

        private List<E> shardCursorGet(int limitSize) {
            List<E> page = new ArrayList<>();
            while (true) {
                // 已经满足, 直接返回
                if (page.size() >= limitSize) {
                    return page;
                }

                // 获取当前shardIndex TODO 可以判断get == shardCursorList.size; (getShardCursorIndex)
                int thisShardIndex = (int) (shardCursor.getAndIncrement() % shardCursorList.size());

                if (thisShardIndex == 0 && checkEmpty()) {
                    return page;
                }

                // 获取当前shard
                ShardCursor thisShardCursor = shardCursorList.get(thisShardIndex);
                // 已经没有了
                if (!thisShardCursor.isHasData()) {
                    continue;
                }

                // 计算剩余需要的量
                int needCount = limitSize - page.size();
                List<E> thisPage =
                        getShardCursor(dao, thisShardCursor.getShardKey(), thisShardCursor.getCursor(), needCount);
                // 已经没有数据了
                if (thisPage.size() < needCount) {
                    thisShardCursor.setHasData(false);
                }

                if (CollectionUtils.isEmpty(thisPage)) {
                    continue;
                }

                thisShardCursor.setCursor(entityToIdFunction.apply(thisPage.get(thisPage.size() - 1)));
                if (dataFilterPredicate == null) {
                    page.addAll(thisPage);
                } else {
                    page.addAll(thisPage.stream().filter(dataFilterPredicate).collect(Collectors.toList()));
                }
            }
        }


        /**
         * 真正执行查询的地方.
         * 从一个特定shard提取数据.
         * 注意：由于dao的实现start是被包含的, 故下一次查询需要去除start
         */
        private List<E> getShardCursor(GetByShardCursorDAO<S, I, E> dao, S shardKey, I start, int limitSize) {
            return dao.getByCursor(shardKey, start, limitSize);
        }


        private List<ShardCursor> buildShardCursorList() {
            return shardKeyList.stream().map(shardKey -> new ShardCursor(true, shardKey, initCursor))
                    .collect(Collectors.toList());
        }

        private boolean checkEmpty() {
            return shardCursorList.stream().noneMatch(ShardCursor::isHasData);
        }

        private int getShardCursorIndex() {
            long shardIndex = shardCursor.get();
            if (shardIndex == shardCursorList.size()) {
                shardCursor.set(0L);
            }
            return (int) (shardCursor.getAndIncrement() % shardCursorList.size());
        }
    }


    private class ShardCursor {
        private boolean hasData;
        private S shardKey;
        private I cursor;

        public ShardCursor() {
        }

        public ShardCursor(boolean hasData, S shardKey, I cursor) {
            this.hasData = hasData;
            this.shardKey = shardKey;
            this.cursor = cursor;
        }

        public boolean isHasData() {
            return hasData;
        }

        public void setHasData(boolean hasData) {
            this.hasData = hasData;
        }

        public S getShardKey() {
            return shardKey;
        }

        public void setShardKey(S shardKey) {
            this.shardKey = shardKey;
        }

        public I getCursor() {
            return cursor;
        }

        public void setCursor(I cursor) {
            this.cursor = cursor;
        }
    }

    /**
     * 构造器
     * @param <S> 分表key类型
     * @param <I> 流标类型
     * @param <E> 数据model
     */

    public static class BatchShardCursorIteratorBuilder<S, I, E> {

        private GetByShardCursorDAO<S, I, E> dao;
        private I initCursor;
        private IntSupplier batchSize;
        private List<S> shardKeyList;
        private Function<E, I> entityToIdFunction;
        private Predicate<E> dataFilterPredicate;

        private BatchShardCursorIteratorBuilder() {
        }

        public static <S, I, E> BatchShardCursorIteratorBuilder<S, I, E> newBuilder() {
            return new BatchShardCursorIteratorBuilder<>();
        }


        /**
         * 设置查询方法
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setDao(GetByShardCursorDAO<S, I, E> dao) {
            this.dao = dao;
            return this;
        }

        /**
         * 设置初始游标
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setInitCursor(I initCursor) {
            this.initCursor = initCursor;
            return this;
        }

        /**
         * 设置每次查询多少条数据
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setBatchSize(IntSupplier batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        /**
         * 设置shardKeyList
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setShardKeyList(List<S> shardKeyList) {
            this.shardKeyList = shardKeyList;
            return this;
        }

        /**
         * 设置数据转换方法
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setEntityToIdFunction(Function<E, I> entityToIdFunction) {
            this.entityToIdFunction = entityToIdFunction;
            return this;
        }

        /**
         * 设置数据过滤器
         */
        public BatchShardCursorIteratorBuilder<S, I, E> setDataFilterPredicate(Predicate<E> dataFilterPredicate) {
            this.dataFilterPredicate = dataFilterPredicate;
            return this;
        }

        public BatchShardCursorIterator<S, I, E> build() {
            if (dao == null || initCursor == null || batchSize == null
                    || entityToIdFunction == null || CollectionUtils.isEmpty(shardKeyList)) {
                throw new RuntimeException("参数不合法!");
            }
            return new BatchShardCursorIterator<>(dao, initCursor, batchSize, shardKeyList, entityToIdFunction,
                    dataFilterPredicate);
        }
    }
}
