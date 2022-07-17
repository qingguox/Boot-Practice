package com.xlg.component.nio;

/**
 * @author wangqingwei
 * Created on 2022-05-28
 */
public interface TestCommon {

    String FILE_PREFIX = "src/files/";
    String FILE_PREFIX_NIO = FILE_PREFIX + "nio/";

    String LOCAL_HOST = "localhost";

    byte SPLIT_STR = '\n';

    int BUFFER_EXPRESSION_FACTOR = 2;

    int NETTY_SERVER_PORT = 8080;
}
