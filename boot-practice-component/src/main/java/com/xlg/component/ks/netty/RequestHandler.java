package com.xlg.component.ks.netty;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author wangqingwei
 * Created on 2022-01-23
 */
public interface RequestHandler {
    Object handle(FullHttpRequest fullHttpRequest);
}
