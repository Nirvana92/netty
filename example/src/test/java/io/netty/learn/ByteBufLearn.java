package io.netty.learn;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * ByteBuf 示例demo
 */
@Slf4j
public class ByteBufLearn {

    @Test
    public void testByteBuf() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(6, 10);
        printBufInfo("ByteBufAllocator.buffer(6, 10)", buffer);

        buffer.writeBytes(new byte[]{1,2});
        printBufInfo("write 2 bytes", buffer);

    }

    public void printBufInfo(String str, ByteBuf buffer) {

        log.info("-------------------{}--------------------", str);
        log.info("readerIndex: {}", buffer.readerIndex());
        log.info("readableBytes: {}", buffer.readableBytes());
        log.info("isReadable: {}", buffer.isReadable());

        log.info("writerIndex: {}", buffer.writerIndex());
        log.info("writableBytes: {}", buffer.writableBytes());
        log.info("isWritable: {}", buffer.isWritable());

        log.info("capacity: {}", buffer.capacity());
        log.info("maxCapacity: {}", buffer.maxCapacity());
        log.info("maxWritableBytes: {}", buffer.maxWritableBytes());
    }
}
