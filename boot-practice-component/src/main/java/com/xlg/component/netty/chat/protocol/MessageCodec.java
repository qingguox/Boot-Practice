package com.xlg.component.netty.chat.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlg.component.netty.chat.message.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 * 自定义编码器与解码器. 其实就是一套通信协议, 就是一个handler
 * 注意: 这个是不能加 @Sharable 也即是线程共享.
 * 因为父类害怕, 子类中存在一些有状态的编解码.
 * @author wangqingwei
 * Created on 2022-06-19
 */
public class MessageCodec extends ByteToMessageCodec<Message> {
    private static final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 1. 设置默数  4字节
        out.writeBytes(new byte[] {'Q', 'W', 'E', 'R'});
        // 2. 版本号    1字节
        out.writeByte(1);
        // 3. 序列化算法 1字节 0: jdk 1:protobuf
        out.writeByte(0);
        // 4. 指令类型   1字节 0: 登陆
        out.writeByte(msg.getMessageType());
        // 5. 请求序号   4字节
        out.writeInt(msg.getSequenceId());
        // 为了补齐16字节, 加一个
        out.writeByte(0xff);
        // 6. 正文长度   4字节
        // 7. 消息正文

        // 获取序列化后的msg
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(msg);

        // 得到jdk序列化后的数据
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        logger.debug("编码");
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 获取魔数
        final int magic = in.readInt();
        // 获取版本号
        final byte version = in.readByte();
        // 获取序列化算法
        final byte seqType = in.readByte();
        // 获取指令类型
        final byte messageType = in.readByte();
        // 请求序号
        final int sequenceId = in.readInt();
        // 额外数据
        final byte extData = in.readByte();
        // 四字节的正文长度
        final int length = in.readInt();
        // 正文内容

        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message msg = (Message)objectInputStream.readObject();
        logger.debug("解码: {}, {}, {}, {}, {}, {}, {}, {}", magic, version, seqType, messageType, sequenceId, extData, length, msg);
        // 下一个handler处理
        out.add(msg);
    }
}
