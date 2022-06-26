package com.xlg.component.netty.chat.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlg.component.netty.chat.message.ChatRequestMessage;
import com.xlg.component.netty.chat.message.GroupChatRequestMessage;
import com.xlg.component.netty.chat.message.GroupCreateRequestMessage;
import com.xlg.component.netty.chat.message.GroupJoinRequestMessage;
import com.xlg.component.netty.chat.message.GroupMembersRequestMessage;
import com.xlg.component.netty.chat.message.GroupQuitRequestMessage;
import com.xlg.component.netty.chat.message.LoginRequestMessage;
import com.xlg.component.netty.chat.message.LoginResponseMessage;
import com.xlg.component.netty.chat.message.PingMessage;
import com.xlg.component.netty.chat.protocol.MessageShareCodec;
import com.xlg.component.netty.chat.protocol.ProtocolFrameDecoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;


public class ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageShareCodec messageSharableCodec = new MessageShareCodec();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        AtomicBoolean EXIT = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(loggingHandler);
                    ch.pipeline().addLast(messageSharableCodec);
                    // 用来判断是不是 读空闲时间过长，或 写空闲时间过长
                    // 3s 内如果没有向服务器写数据，会触发一个 IdleState#WRITER_IDLE 事件
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // ChannelDuplexHandler 可以同时作为入站和出站处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // 触发了写空闲事件
                            if (event.state() == IdleState.WRITER_IDLE) {
                                //                                log.debug("3s 没有写数据了，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    ch.pipeline().addLast("login handler", new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            logger.debug("client receive login resp msg : {}", msg);
                            if ((msg instanceof LoginResponseMessage)) {
                                LoginResponseMessage responseMessage = (LoginResponseMessage)msg;
                                if (responseMessage.isSuccess()) {
                                    // 设置登陆的状态
                                    LOGIN.set(true);
                                }
                                // 唤醒
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        /**
                         * 创建连接时执行的处理器，用于执行登陆操作
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 开辟额外线程，用于用户登陆及后续操作
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                // 创建包含登录信息的请求体
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(message);

                                System.out.println("等待后续操作...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                    // 登陆失败
                                    if (!LOGIN.get()) {
                                        ctx.channel().close();
                                        return;
                                    }

                                    // 登陆后的其他操作
                                    while(true) {
                                        System.out.println("==================================");
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");
                                        String command = scanner.nextLine();
                                        // 获得指令及其参数，并发送对应类型消息
                                        String[] commands = command.split(" ");
                                        switch (commands[0]){
                                            case "send":
                                                ctx.writeAndFlush(new ChatRequestMessage(username, commands[1], commands[2]));
                                                break;
                                            case "gsend":
                                                ctx.writeAndFlush(new GroupChatRequestMessage(username,commands[1], commands[2]));
                                                break;
                                            case "gcreate":
                                                // 分割，获得群员名
                                                String[] members = commands[2].split(",");
                                                Set<String> set = new HashSet<>(Arrays.asList(members));
                                                // 把自己加入到群聊中
                                                set.add(username);
                                                ctx.writeAndFlush(new GroupCreateRequestMessage(commands[1], set));
                                                break;
                                            case "gmembers":
                                                ctx.writeAndFlush(new GroupMembersRequestMessage(commands[1]));
                                                break;
                                            case "gjoin":
                                                ctx.writeAndFlush(new GroupJoinRequestMessage(username, commands[1]));
                                                break;
                                            case "gquit":
                                                ctx.writeAndFlush(new GroupQuitRequestMessage(username, commands[1]));
                                                break;
                                            case "quit":
                                                ctx.channel().close();
                                                return;
                                            default:
                                                System.out.println("指令有误，请重新输入");
                                                continue;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }, "system in").start();
                        }

                        // 在连接断开时触发
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            logger.debug("连接已经断开，按任意键退出..");
                            EXIT.set(true);
                        }

                        // 在出现异常时触发
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            logger.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                            EXIT.set(true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
