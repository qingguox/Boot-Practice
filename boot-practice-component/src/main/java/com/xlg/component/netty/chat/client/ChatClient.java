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
        Scanner scanner = new Scanner(System.in);
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
                    // ????????????????????? ??????????????????????????? ?????????????????????
                    // 3s ?????????????????????????????????????????????????????? IdleState#WRITER_IDLE ??????
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // ChannelDuplexHandler ??????????????????????????????????????????
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // ????????????????????????
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                            IdleStateEvent event = (IdleStateEvent) evt;
                            // ????????????????????????
                            if (event.state() == IdleState.WRITER_IDLE) {
                                //                                log.debug("3s ??????????????????????????????????????????");
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
                                    // ?????????????????????
                                    LOGIN.set(true);
                                }
                                // ??????
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        /**
                         * ????????????????????????????????????????????????????????????
                         */
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // ??????????????????????????????????????????????????????
                            new Thread(() -> {
                                System.out.println("??????????????????:");
                                String username = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                System.out.println("???????????????:");
                                String password = scanner.nextLine();
                                if(EXIT.get()){
                                    return;
                                }
                                // ??????????????????
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                System.out.println(message);
                                // ????????????
                                ctx.writeAndFlush(message);
                                System.out.println("??????????????????...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                    // ????????????
                                    if (!LOGIN.get()) {
                                        ctx.channel().close();
                                        return;
                                    }

                                    // ????????????????????????
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
                                        String command = null;
                                        try {
                                            command = scanner.nextLine();
                                        } catch (Exception e) {
                                            break;
                                        }
                                        if(EXIT.get()){
                                            return;
                                        }
                                        // ??????????????????????????????????????????????????????
                                        String[] commands = command.split(" ");
                                        switch (commands[0]){
                                            case "send":
                                                ctx.writeAndFlush(new ChatRequestMessage(username, commands[1], commands[2]));
                                                break;
                                            case "gsend":
                                                ctx.writeAndFlush(new GroupChatRequestMessage(username,commands[1], commands[2]));
                                                break;
                                            case "gcreate":
                                                // ????????????????????????
                                                String[] members = commands[2].split(",");
                                                Set<String> set = new HashSet<>(Arrays.asList(members));
                                                // ???????????????????????????
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
                                                System.out.println("??????????????????????????????");
                                                continue;
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }, "system in").start();
                        }

                        // ????????????????????????
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            logger.debug("???????????????????????????????????????..");
                            EXIT.set(true);
                        }

                        // ????????????????????????
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            logger.debug("???????????????????????????????????????..{}", cause.getMessage());
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
