package com.xlg.component.ks.netty;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;

import com.google.common.collect.Maps;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @author wangqingwei
 * Created on 2022-01-23
 */
public class GetRequestHandler implements RequestHandler {

    @Override
    public Object handle(FullHttpRequest fullHttpRequest) {
        String requestUri = fullHttpRequest.uri();
        return getQueryParams(requestUri).toString();
    }

    private Map<String, String> getQueryParams(String uri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
        Map<String, List<String>> parameters = queryDecoder.parameters();
        Map<String, String> result = Maps.newHashMap();
        parameters.forEach((key, value) -> value.forEach(curValue -> {
            result.put(key, curValue);
        }));
        return result;
    }
}
