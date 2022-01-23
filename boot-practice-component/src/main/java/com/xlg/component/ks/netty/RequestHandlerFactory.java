package com.xlg.component.ks.netty;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import io.netty.handler.codec.http.HttpMethod;

/**
 * @author wangqingwei
 * Created on 2022-01-23
 * 请求处理工厂, 后面可以写成Service
 */
public class RequestHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerFactory.class);
    private static final Map<HttpMethod, RequestHandler> map = Maps.newHashMap();

    static {
        map.put(HttpMethod.GET, new GetRequestHandler());
        map.put(HttpMethod.POST, new PostRequestHandler());
    }

    public static RequestHandler create(HttpMethod httpMethod) {
        return map.get(httpMethod);
    }
}
