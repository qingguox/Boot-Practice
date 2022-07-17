package com.xlg.component.netty.apply.protocol;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlg.component.netty.apply.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

/**
 * msgToMsg 不会出现需要多个channel共享时出现半包记录数据问题.
 *
 * 需要和 new LengthFieldBasedFrameDecoder 一起使用. 确保buf数据是完整的.
 * @author wangqingwei
 * Created on 2022-06-19
 */
@Sharable
public class MessageShareCodec extends MessageToMessageCodec<ByteBuf, Message> {
    private static final Logger logger = LoggerFactory.getLogger(MessageShareCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        final ByteBuf buf = ctx.alloc().buffer();
        // 1. 设置默数  4字节
        buf.writeBytes(new byte[] {'Q', 'W', 'E', 'R'});
        // 2. 版本号    1字节
        buf.writeByte(1);
        // 3. 序列化算法 1字节 0: jdk 1:protobuf
        buf.writeByte(0);
        // 4. 指令类型   1字节 0: 登陆
        buf.writeByte(msg.getMessageType());
        // 5. 请求序号   4字节
        buf.writeInt(msg.getSequenceId());
        // 为了补齐16字节, 加一个
        buf.writeByte(0xff);
        // 6. 正文长度   4字节
        // 7. 消息正文

        // 获取序列化后的msg
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(msg);

        // 得到jdk序列化后的数据
        byte[] bytes = byteArrayOutputStream.toByteArray();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        logger.debug("编码");
        out.add(msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {

    }
}
