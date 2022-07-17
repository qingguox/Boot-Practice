package com.xlg.component.netty.ByteBuf;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.xlg.component.ks.utils.ByteBufUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author wangqingwei
 * Created on 2022-06-12
 */
public class ByteBufTest {

    @Test
    public void byteBufStudy(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        // init
        ByteBufUtil.log(buffer);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            stringBuilder.append("a");
        }

        buffer.writeBytes(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
        ByteBufUtil.log(buffer);
    }

    @Test
    public void directAndHeapByteBuf(){
        // 默认是以直接内存
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(16);
        System.out.println(buffer.getClass());

        // 堆内存创建
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(16);
        System.out.println(buf.getClass());

        ByteBuf buffer2 = ByteBufAllocator.DEFAULT.directBuffer(16);
        System.out.println(buffer2.getClass());
    }

}
