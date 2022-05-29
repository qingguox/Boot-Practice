package com.xlg.component.nio;

import static com.xlg.component.ks.utils.ByteBufferUtil.debugAll;
import static com.xlg.component.nio.TestCommon.FILE_PREFIX_NIO;
import static com.xlg.component.nio.TestCommon.SPLIT_STR;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangqingwei
 * Created on 2022-05-28
 */
public class ByteBufferTest {

    private static final Logger logger = LoggerFactory.getLogger(ByteBufferTest.class);
    private static final String INPUT_FILE_NAME = FILE_PREFIX_NIO + "testByteBufferText.txt";
    private static final int BUFFER_CAPACITY = 10;


    @Test
    public void testByteBufferRead() {
        // 1. 获取FileChannel
        try (FileChannel channel = new FileInputStream(INPUT_FILE_NAME).getChannel()) {
            // 2. 获取缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            StringBuilder bufferData = new StringBuilder();
            while (channel.read(buffer) > 0) {
                // 3. 切换buffer为读模式, limit=position, position=0
                buffer.flip();
                // 4. buffer是否还有数据
                while (buffer.hasRemaining()) {
                    bufferData = bufferData.append((char)buffer.get());
                }
                // 5. 切换buffer为写模式, limit=capacity, position=0
                buffer.clear();
                debugAll(buffer);
            }
            System.out.println(bufferData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStrBuffer2ConvertVersion1() {
        String str = "hello nio!";
        String result;

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(str.getBytes());
        debugAll(buffer);

        buffer.flip();

        result = StandardCharsets.UTF_8.decode(buffer).toString();
        debugAll(buffer);
        System.out.println("version1 result : " + result);
    }


    @Test
    public void testStrBuffer2ConvertVersion2() {
        String str = "hello nio!";
        String result;

        final ByteBuffer buffer = StandardCharsets.UTF_8.encode(str);

        result = StandardCharsets.UTF_8.decode(buffer).toString();
        debugAll(buffer);
        System.out.println("version2 result : " + result);
    }

    @Test
    public void testStrBuffer2ConvertVersion3() {
        String str = "hello nio!";
        String result;

        final ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        result = StandardCharsets.UTF_8.decode(buffer).toString();
        debugAll(buffer);
        System.out.println("version3 result : " + result);
    }

    /**
     * 利用特殊符号分隔粘包
     */
    @Test
    public void processStickPck() {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        // 模拟粘包+半包
        buffer.put("Hello,world\nI'm Nyima\nHo".getBytes());



        stickPckSplit(buffer, SPLIT_STR);
        buffer.put("w are you?\n".getBytes());
        stickPckSplit(buffer, SPLIT_STR);
    }

    public void stickPckSplit(ByteBuffer buffer, byte splitStr) {
        buffer.flip();

        for (int index = 0; index < buffer.limit(); index++) {
            final byte element = buffer.get(index);
            if (element == splitStr) {
                int subLength = index - buffer.position();
                ByteBuffer subBuffer = ByteBuffer.allocate(subLength);
                for (int subIndex = 0; subIndex < subLength; subIndex++) {
                    subBuffer.put(buffer.get());
                }
                // 有一位是\n
                buffer.get();
                debugAll(subBuffer);
            }
        }
        // 因为有些数据还没有拿完
        buffer.compact();
//        debugAll(buffer);
    }

}
