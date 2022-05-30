package io.netty.example.nio.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/** 固定长度的socket 的读取工具类 */
@Slf4j
public class FixedLengthSocketReader implements SocketReader {

  // 固定长度, 可以通过构造器设置或者set 方法设置
  private int fixedLength = 10;

  @Override
  public String read(SocketChannel channel) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(fixedLength);
    try {
      int read = channel.read(buffer);
      if (read > 0) {
        String str = new String(buffer.array());
        log.info("读取到的内容: {}", str);
        return str;
      }
    } catch (IOException e) {
      //            throw new RuntimeException(e);
      log.error("固定长度读取器异常: {}", e.getLocalizedMessage());
    }
    return null;
  }
}
