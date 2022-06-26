package com.xlg.component.netty.chat.protocol;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.xlg.component.netty.chat.config.Config;
import com.xlg.component.netty.chat.message.Message;
import com.xlg.component.netty.chat.protocol.Serializer.Algorithm;

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

    @Value("${serializer.algorithm}")
    private String algorithm;

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        buf.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        buf.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 4. 1 字节的指令类型
        buf.writeByte(msg.getMessageType());
        // 5. 4 个字节
        buf.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        buf.writeByte(0xff);
//        // 6. 获取内容的字节数组
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(msg);
//        byte[] bytes = bos.toByteArray();
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        buf.writeInt(bytes.length);
        // 8. 写入内容
        buf.writeBytes(bytes);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 获取魔数
        int magic = in.readInt();
        // 获取版本号
        byte version = in.readByte();
        // 获取序列化算法
        byte seqType = in.readByte();
        // 获取指令类型
        byte messageType = in.readByte();
        // 请求序号
        int sequenceId = in.readInt();
        // 额外数据
        byte extData = in.readByte();
        // 四字节的正文长度
        int length = in.readInt();
        // 正文内容

        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
//        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        Message msg = (Message)objectInputStream.readObject();
        Algorithm algorithm = Algorithm.values()[seqType];
        Message msg = (Message) algorithm.deserialize(Message.getMessageClass(messageType), bytes);
        //        logger.debug("解码: {}, {}, {}, {}, {}, {}, {}, {}", magic, version, seqType, messageType, sequenceId, extData, length, msg);
        // 下一个handler处理
        logger.debug("{}", msg);
        out.add(msg);
    }
}
