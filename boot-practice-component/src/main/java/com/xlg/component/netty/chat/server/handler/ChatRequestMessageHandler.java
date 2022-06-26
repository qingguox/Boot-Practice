package com.xlg.component.netty.chat.server.handler;

import com.xlg.component.netty.chat.message.ChatRequestMessage;
import com.xlg.component.netty.chat.message.ChatResponseMessage;
import com.xlg.component.netty.chat.server.session.SessionFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangqingwei
 * Created on 2022-06-26
 */
@Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        // 对方在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            // 对方不在线
            ctx.writeAndFlush(new ChatResponseMessage(false, "用户: " + to + "不在线!!"));
        }
    }
}
