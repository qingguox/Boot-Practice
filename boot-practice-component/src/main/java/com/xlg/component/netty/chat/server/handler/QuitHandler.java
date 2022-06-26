package com.xlg.component.netty.chat.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlg.component.netty.chat.server.session.SessionFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wangqingwei
 * Created on 2022-06-26
 */
@Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(QuitHandler.class);

    /**
     * 连接断开时触发
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        logger.debug("{} channel is inactive!", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        logger.debug("{} channel has exception! e : {}", ctx.channel(), cause.getMessage());
    }
}
