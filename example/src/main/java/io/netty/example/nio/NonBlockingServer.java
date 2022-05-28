package io.netty.example.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/** 通过ServerSocketChannel 创建非阻塞的服务端 */
@Slf4j
public class NonBlockingServer {

  public static void main(String[] args) throws IOException, InterruptedException {

    List<SocketChannel> clients = new ArrayList<>();

    ServerSocketChannel ssc = ServerSocketChannel.open();
    ssc.bind(new InetSocketAddress("localhost", 19999));
    // 设置非阻塞: 表示下面ssc.accept() 方法调用的时候不会进行阻塞
    ssc.configureBlocking(false);
    // 参数配置
    // ssc.setOption()

    while (true) {
      SocketChannel client = ssc.accept();
      if (Objects.nonNull(client)) {
        // 有连接进来了/

        // 设置为非阻塞
        client.configureBlocking(false);

        clients.add(client);
        log.info("client: {}", client);
      }

      ByteBuffer buffer = ByteBuffer.allocate(1024);
      // 遍历客户端的连接进行读取操作
      for (SocketChannel channel : clients) {
        int read = channel.read(buffer);

        if(read > 0) {
          // 说明有数据, 进行数据的读取

        }
      }

      Thread.sleep(2000);
    }
  }
}
