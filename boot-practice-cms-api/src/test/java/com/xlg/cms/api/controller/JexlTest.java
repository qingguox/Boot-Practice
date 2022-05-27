package com.xlg.cms.api.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.xlg.component.ks.jexl.JexlService;
import com.xlg.component.ks.utils.ThreadExecutorHolder;

/**
 * @author wangqingwei
 * Created on 2022-05-27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JexlTest {

    @Autowired
    private JexlService jexlService;

    @Test
    public void testJexl() {
        ExecutorService executorService = ThreadExecutorHolder.getIndicatorProcessExecutor();

        String templateParam = "money";
        int templateParamBaseValue = 600;
        int expressionListSize = 10;
        final Map<String, Object> paramMap = ImmutableMap.of(templateParam, templateParamBaseValue);
        List<String> expressionList = Lists.newArrayList();
        Stream.iterate(0, seed -> seed + 1)
                .limit(expressionListSize)
                .forEach(element -> expressionList.add(templateParam + "+" + element));
        List<Future<Object>> tasks = expressionList
                .stream()
                .map(expression -> executorService.submit(() -> jexlService.convertExpression(expression, paramMap)))
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            try {
                Object o = task.get();
                System.out.println(o);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
