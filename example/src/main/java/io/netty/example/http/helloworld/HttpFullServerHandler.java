package io.netty.example.http.helloworld;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class HttpFullServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger log = LoggerFactory.getLogger(HttpFullServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        log.info("method: {}, uri: {}, params: {}", msg.method(), msg.uri(), msg.content().toString(Charset.forName("utf-8")));

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
        fullHttpResponse.headers().add("Content-Type", "text/html; charset=UTF-8");
        fullHttpResponse.content().writeBytes("<html><head><title>Hello World</title></head><body>Hello World</body></html>".getBytes());
        // 如果没有设置Content-Length，浏览器会一直等待，直到超时
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
        ctx.writeAndFlush(fullHttpResponse);
    }
}
