package com.xlg.component.netty.apply.protocol;

import com.xlg.component.netty.apply.message.LoginRequestMessage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author wangqingwei
 * Created on 2022-06-19
 */
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                // 长度字段. 解决半包与粘包问题.
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0,0),
                new MessageCodec()
        );

        // 添加编码器
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123", "张三");
//        channel.writeOutbound(message);

        // 测试解码
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, byteBuf);

        final ByteBuf slice1 = byteBuf.slice(0, 100);
        final ByteBuf slice2 = byteBuf.slice(100, byteBuf.readableBytes() - 100);
        slice1.retain();

        channel.writeInbound(slice1);  // release 1
        channel.writeInbound(slice2);
//        channel.writeInbound(byteBuf);
    }
}
