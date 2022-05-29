package com.xlg.component.nio;


import static com.xlg.component.nio.TestCommon.FILE_PREFIX_NIO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

/**
 * @author wangqingwei
 * Created on 2022-05-28
 */
public class FileChannelTest {

    private static final String TRANSFER_TWO_CHANNEL_INPUT = FILE_PREFIX_NIO + "input.txt";
    private static final String TRANSFER_TWO_CHANNEL_OUT = FILE_PREFIX_NIO + "output.txt";

    private static final String FILE_MOVE_SOURCE = FILE_PREFIX_NIO + "fileMoveData.txt";
    private static final String FILE_MOVE_TARGET = FILE_PREFIX_NIO + "fileMoveTarget.txt";

    /**
     * 使用transferTo方法可以快速、高效地将一个channel中的数据传输到另一个channel中，但一次只能传输2G的内容
     *
     * transferTo底层使用了零拷贝技术
     */
    @Test
    public void testTwoChannelTransfer() {
        try (FileChannel inputChannel = new FileInputStream(TRANSFER_TWO_CHANNEL_INPUT).getChannel();
                FileChannel outChannel = new FileOutputStream(TRANSFER_TWO_CHANNEL_OUT).getChannel();
                ) {
            // 参数：inputChannel的起始位置，传输数据的大小，目的channel
            // 返回值为传输的数据的字节数
            // transferTo一次只能传输2G的数据
            inputChannel.transferTo(0, inputChannel.size(), outChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 当传输的文件大于2G时，需要使用以下方法进行多次传输
     */
    @Test
    public void testTwoChannelTransferV2() {
        try (FileChannel inputChannel = new FileInputStream(TRANSFER_TWO_CHANNEL_INPUT).getChannel();
                FileChannel outChannel = new FileOutputStream(TRANSFER_TWO_CHANNEL_OUT).getChannel();
        ) {
            long size = inputChannel.size();
            long capacity = inputChannel.size();
            while (capacity > 0) {
                // // transferTo返回值为传输了的字节数
                capacity -= inputChannel.transferTo(size - capacity, capacity, outChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPath() {
        Path source = Paths.get("1.txt"); // 相对路径 不带盘符 使用 user.dir 环境变量来定位 1.txt

        source = Paths.get("d:\\1.txt"); // 绝对路径 代表了  d:\1.txt 反斜杠需要转义

        source = Paths.get("d:/1.txt"); // 绝对路径 同样代表了  d:\1.txt

        Path projects = Paths.get("d:\\data", "projects"); // 代表了  d:\data\projects
    }

    @Test
    public void testFiles() throws IOException {
        Path source = Paths.get(FILE_MOVE_SOURCE);
        Path target = Paths.get(FILE_MOVE_TARGET);

        // copy文件 如果target 存在, 即覆盖
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        // 移动文件
        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }
}
