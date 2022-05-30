package io.netty.example.nio.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/** 分隔符socket 读取器 */
@Slf4j
public class SeparatorSocketReader implements SocketReader {

  private final String SEPARATOR = "$";

  @Override
  public String read(SocketChannel channel) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(5);

    int read = channel.read(buffer);
    if (read > 0) {
      // TODO: 2022/5/30 未实现
    }

    return null;
  }
}
