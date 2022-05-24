package io.netty.learn;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/** buffer的一些使用 */
public class BufferLearn {

  private static Unsafe unsafe = null;

  @Test
  public void byteBufferTest() {

    // 第一种申请堆外内存的方式
    // 可以自己回收, 内部实现还是通过 unsafe#allocateMemory 来实现的
    ByteBuffer buffer = ByteBuffer.allocateDirect(10 * 1024 * 1024);


    // 第二种申请堆外内存的方式[通过Unsafe 的方式]
    try {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      // 获得 unsafe
      // 通过unsafe 生成的堆外内存需要手动释放
      unsafe = (Unsafe) theUnsafe.get(null);

      // 分配堆外内存
      long address = unsafe.allocateMemory(10 * 1024 * 1024);

      // 释放对外内存
      unsafe.freeMemory(address);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
