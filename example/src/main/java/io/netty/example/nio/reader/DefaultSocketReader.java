package io.netty.example.nio.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/** 默认的读取方式实现 */
@Slf4j
public class DefaultSocketReader implements SocketReader {

  @Override
  public String read(SocketChannel channel) throws IOException {
    // System.out.println(selectionKey.channel());
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    int read = channel.read(byteBuffer);

    if (read > 0) {
      String str = new String(byteBuffer.array());
      // System.out.println(str);
      log.info("服务端读取的数据: {}", str);
      return str;
    }
    return null;
  }
}
