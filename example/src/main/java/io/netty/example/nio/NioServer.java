package io.netty.example.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

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

    private ServerSocketChannel ssc = null;
    private Selector selector = null;

    private final String hostName = "localhost";
    private final Integer port = 99999;

    /**
     * 初始化服务端
     */
    public void initServer() throws IOException {
        ssc = ServerSocketChannel.open();
        // 设置非阻塞
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress(hostName, port));

        // 优先选择epoll . 可以通过 -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.PollSelectorProvider 来调整
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 启动服务
     * @throws IOException
     */
    public void start() throws IOException {
        initServer();

        while (true) {
            int select = selector.select(1000);
            if(select > 0) {
                // 拿到的有状态的fd 结果集
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if(selectionKey.isAcceptable()) {
                        acceptChannel(selector, selectionKey);
                    }else if(selectionKey.isReadable()) {
                        // 读取链接的客户端发送的消息
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        String readMessage = read(channel);
                        // 回写
                        write(channel, readMessage);
                    }
                    // 操作完成之后需要进行事件的移除, 如果不进行移除会重复处理
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 接受客户端的链接
     * @param selector
     * @param selectionKey
     * @throws IOException
     */
    public void acceptChannel(Selector selector, SelectionKey selectionKey) throws IOException {
        // System.out.println(selectionKey.channel());
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

        // 接受客户端的新链接
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 注册监听读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 从客户端的连接读取消息
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
