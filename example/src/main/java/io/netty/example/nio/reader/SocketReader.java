package io.netty.example.nio.reader;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * socket 读取工具类
 */
public interface SocketReader {

    String read(SocketChannel channel) throws IOException;
}
