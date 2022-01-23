package com.xlg.component.ks.netty.test1;

import java.util.Arrays;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * @author wangqingwei
 * Created on 2022-01-23
 */
public class PostRequestHandler implements RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(PostRequestHandler.class);

    @Override
    public Object handle(FullHttpRequest fullHttpRequest) {
        String requestUri = fullHttpRequest.uri();
        logger.info("request uri : [{}]", requestUri);
        String contentType = getContentType(fullHttpRequest.headers());
        if (contentType.equals("application/json")) {
            return fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
        } else {
            throw new IllegalArgumentException("only receive application/json type data");
        }
    }

    private String getContentType(HttpHeaders headers) {
        String typeStr = headers.get("Content-Type");
        String[] split = typeStr.split(";");
        return Arrays.stream(split).findFirst().orElse("");
    }
}
