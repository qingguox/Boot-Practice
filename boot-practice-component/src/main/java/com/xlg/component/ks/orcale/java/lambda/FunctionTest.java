package com.xlg.component.ks.orcale.java.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author wangqingwei
 * Created on 2022-03-25
 *
 *  供货商: 不要争论(不会传参), 返回东西
 *  消费者: 接受一个论点, 不返回东西
 *  谓词: 接受一个参数, 返回一个布尔值
 *  函数: 接受一个参数, 返回一些东西
 */
public class FunctionTest {

    @Test
    public void testSupplier() {
        Random random = new Random(10);
        Supplier<Integer> supplier = () -> random.nextInt(5);

        for (int i = 0; i < 10; i++) {
            System.out.println(supplier.get() + "-");
        }
    }

    @Test
    public void testConsumer() {
        Consumer<String> consumer = System.out::println;

        for (int i = 0; i < 7; i++) {
            consumer.accept(i + "");
        }
    }

    @Test
    public void testPredicate() {
        String world = "hello world";
        Predicate<String> predicate = s -> s.length() == 0;
        System.out.println(predicate.test(world));
    }

    @Test
    public void testFunction() {
        Function<String, Integer> function = String::length;
        Integer hello_world = function.apply("hello world");
        System.out.println(hello_world);
    }

    @Test
    public void test() {
        List<String> models = Arrays.asList("1", "22", "333", "4", "55", "666");
        List<Integer> integers = shardOperation(models, 2, this::shardKey, this::shardKeyWithShard);
        System.out.println(integers);
    }

    private List<Integer> shardKeyWithShard(int shard, List<String> models) {
        System.out.println("shard : " + shard);
        System.out.println("models : {}" + models);
        return models.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public int shardKey(String key) {
        return key.length() % 3;
    }

    /**
     * 将一个集合的数据(集合中数据类型为M) 进行分组 (分组key为K, 使用KeyFunction完成M到K的转换)
     * 分组后每组在按照batchSize的数量，使用operationFunction进行处理, 最终返回类型为R的结果
     * K必须能做map的key
     */
    public static <K, M, R> List<R> shardOperation(Collection<M> models, int batchSize,
            Function<M, K> keyFunction, BiFunction<K, List<M>, List<R>> operationFunction) {
        if (CollectionUtils.isEmpty(models)) {
            return Collections.emptyList();
        }
        Map<K, List<M>> shardMap = Maps.newHashMap();
        models.forEach(model -> {
            K shardKey = keyFunction.apply(model);
            List<M> modelList = shardMap.computeIfAbsent(shardKey, key -> new ArrayList<>());
            modelList.add(model);
        });

        List<R> resultList = new ArrayList<>(models.size());
        shardMap.forEach((shardKey, modelList) -> Lists.partition(modelList, batchSize).forEach(part -> {
            List<R> partResult = operationFunction.apply(shardKey, part);
            resultList.addAll(partResult);
        }));
        return resultList;
    }
}
