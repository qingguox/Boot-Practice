package com.xlg.cms.api.controller.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xlg.cms.api.model.Result;

/**
 * @author wangqingwei
 * Created on 2022-07-11
 */
@RestController
public class RocketMqUtilsTest {

    /**
     * 任务状态枚举
     */
    @RequestMapping("/redis")
    @ResponseBody
    public Result status() {
        return Result.ok(true);
    }
}
