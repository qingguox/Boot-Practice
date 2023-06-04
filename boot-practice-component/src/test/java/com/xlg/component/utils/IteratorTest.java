package com.xlg.component.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.xlg.component.ks.utils.BatchShardCursorIterator;
import com.xlg.component.ks.utils.BatchShardCursorIterator.BatchShardCursorIteratorBuilder;

/**
 * @author
 * Created on 2023-05-27
 */
public class IteratorTest {


    @Test
    public void test() {
        // 多表
        List<Long> shardKeyList = Stream.iterate(0L, seed -> seed + 1).limit(1000L).collect(Collectors.toList());
        BatchShardCursorIterator<Long, Long, UserInfo> build = BatchShardCursorIteratorBuilder.<Long, Long, UserInfo> newBuilder()
                .setBatchSize(() -> 1000)
                .setShardKeyList(shardKeyList)
                .setInitCursor(0L)
                .setEntityToIdFunction(UserInfo::getId)
                .setDao((shardKey, minId, limit) -> getByMinId(shardKey, minId, limit))
                .build();
    }

    @Test
    public void test2() {
        // 单表
        long mainId = 0L;
        BatchShardCursorIterator<Long, Long, UserInfo> build = BatchShardCursorIteratorBuilder.<Long, Long, UserInfo> newBuilder()
                .setBatchSize(() -> 1000)
                .setShardKeyList(Collections.singletonList(0L))
                .setInitCursor(0L)
                .setEntityToIdFunction(UserInfo::getId)
                .setDao((shardKey, minId, limit) -> getByMinId(mainId, minId, limit))
                .build();
    }

    private List<UserInfo> getByMinId(Long shardKey, Long minId, int limit) {
        return Collections.emptyList();
    }

    public static class UserInfo {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
