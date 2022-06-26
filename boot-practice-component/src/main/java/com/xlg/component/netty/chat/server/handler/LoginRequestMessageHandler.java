package com.xlg.component.netty.chat.server.handler;

import com.xlg.component.netty.chat.message.LoginRequestMessage;
import com.xlg.component.netty.chat.message.LoginResponseMessage;
import com.xlg.component.netty.chat.server.service.UserServiceFactory;
import com.xlg.component.netty.chat.server.session.SessionFactory;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author wangqingwei
 * Created on 2022-06-26
 */
@Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg)
            throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        String reason = "登陆成功";
        if (login) {
            // 绑定channel和用户, 后面方便查
            SessionFactory.getSession().bind(ctx.channel(), username);
        } else {
            reason = "用户名或者密码错误";
        }
        LoginResponseMessage message = new LoginResponseMessage(login, reason);
        ctx.writeAndFlush(message);
    }
}
