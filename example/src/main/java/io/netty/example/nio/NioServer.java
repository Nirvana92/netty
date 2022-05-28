package io.netty.example.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nirvana
 * @date 2021/12/21 22:33
 */
@Slf4j
public class NioServer {

  public static void main(String[] args) throws IOException {
    NioServer nioServer = new NioServer();
    nioServer.start();
  }

  private ExecutorService listenThreadPools = null;
  private ExecutorService operateThreadPools = null;

  private ServerSocketChannel ssc = null;
  // 监听 selector
  private Selector listenSelector = null;
  // 读取操作 selector
  private Selector operateSelector = null;

  private final String hostName = "localhost";
  private final Integer port = 9999;

  private boolean stopServer = false;

  /** 初始化服务端 */
  public void initServer() throws IOException {
    ssc = ServerSocketChannel.open();
    // 设置非阻塞
    ssc.configureBlocking(false);
    ssc.socket().bind(new InetSocketAddress(hostName, port));

    // 优先选择epoll . 可以通过 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.PollSelectorProvider 来调整
    listenSelector = Selector.open();
    ssc.register(listenSelector, SelectionKey.OP_ACCEPT);

    operateSelector = Selector.open();

    listenThreadPools = Executors.newFixedThreadPool(1);
    operateThreadPools = Executors.newFixedThreadPool(1);
  }

  /**
   * 启动服务
   *
   * @throws IOException
   */
  public void start() throws IOException {
    initServer();

    syncListen();
    syncOperate();
  }

  private void syncListen() {
    // 链接监听循环
    listenThreadPools.execute(
        () -> {
          try {
            while (true) {
              int select = listenSelector.select(1000);
              if (select > 0) {
                // 拿到的有状态的fd 结果集
                Iterator<SelectionKey> iterator = listenSelector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                  SelectionKey selectionKey = iterator.next();
                  if (selectionKey.isAcceptable()) {
                    acceptChannel(selectionKey);
                  }
                  // 操作完成之后需要进行事件的移除, 如果不进行移除会重复处理
                  iterator.remove();
                }
              }

              if (stopServer) {
                return;
              }
            }
          } catch (Exception e) {
            log.error("监听异常: {}", e.getLocalizedMessage());
          }
        });
  }

  private void syncOperate() {
    // 业务处理
    operateThreadPools.execute(
        () -> {
          while (true) {
            try {
              int select = operateSelector.select(1000);
              if (select > 0) {
                Iterator<SelectionKey> iterator = operateSelector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                  SelectionKey selectionKey = iterator.next();
                  if (selectionKey.isReadable()) {
                    // 读取链接的客户端发送的消息
                    SocketChannel channel = (SocketChannel) selectionKey.channel();
                    String readMessage = read(channel);
                    // 回写
                    write(channel, readMessage);
                  }

                  iterator.remove();
                }
              }

              if (stopServer) {
                return;
              }
            } catch (IOException e) {
              log.error("读写异常: {}", e.getLocalizedMessage());
            }
          }
        });
  }

  /**
   * 接受客户端的链接
   *
   * @param selectionKey
   * @throws IOException
   */
  public void acceptChannel(SelectionKey selectionKey) throws IOException {
    // System.out.println(selectionKey.channel());
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

    // 接受客户端的新链接
    SocketChannel socketChannel = serverSocketChannel.accept();
    // 设置非阻塞
    socketChannel.configureBlocking(false);

    // 注册到operateSelector
    // 注册监听读事件
    socketChannel.register(operateSelector, SelectionKey.OP_READ);
  }

  /**
   * 从客户端的连接读取消息
   *
   * @param channel 客户端连接
   * @return
   * @throws IOException
   */
  public String read(SocketChannel channel) throws IOException {
    // System.out.println(selectionKey.channel());
    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    channel.read(byteBuffer);

    String str = new String(byteBuffer.array());
    // System.out.println(str);
    log.info("服务端读取的数据: {}", str);
    return str;
  }

  /**
   * 写回消息到客户端
   *
   * @param client 客户端简历的连接
   * @param message 需要发送的消息
   * @throws IOException
   */
  public static void write(SocketChannel client, String message) throws IOException {
    // 写回客户端
    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    writeBuffer.put(message.getBytes());
    writeBuffer.flip();
    client.write(writeBuffer);
  }
}
