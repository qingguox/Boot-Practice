package com.xlg.component.netty.rpc.client.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.xlg.component.netty.chat.message.RpcResponseMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

/**
 * @author wangqingwei
 * Created on 2022-06-27
 */
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    private static final Logger logger = LoggerFactory.getLogger(RpcResponseMessageHandler.class);

    public static final Map<Integer, Promise<Object>> PROMISE = Maps.newConcurrentMap();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) {
        logger.debug("msg : {}", msg);

        Promise<Object> promise = PROMISE.remove(msg.getSequenceId());
        if (promise != null) {
            final Exception exceptionValue = msg.getExceptionValue();
            final Object returnValue = msg.getReturnValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
