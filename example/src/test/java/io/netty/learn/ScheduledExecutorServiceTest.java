package io.netty.learn;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ScheduledExecutorService 的测试用例
 */
@Slf4j
public class ScheduledExecutorServiceTest {

    ScheduledExecutorService scheduled = null;

    @Before
    public void before() {
        scheduled = Executors.newScheduledThreadPool(1);
    }

    @Test
    public void testExecutor() throws IOException {
        long delay = 10, initDelay = 5;
        log.info("开始执行: {}", System.currentTimeMillis());
        // Runnable 在延迟 delay 之后执行
//        scheduled.schedule(() -> {
//            log.info("定时线程池执行: {}", System.currentTimeMillis());
//        }, delay, TimeUnit.SECONDS);

        // 初始延迟 initDelay, 执行一次Runnable, 往后每次延迟delay 之后执行Runnable
//        scheduled.scheduleAtFixedRate(() -> {
//            log.info("定时线程池执行: {}", System.currentTimeMillis());
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }, initDelay, delay, TimeUnit.SECONDS);

        // 和scheduleAtFixedRate 的区别。是继上一个任务执行完成之后延迟delay 之后执行Runnable
        scheduled.scheduleWithFixedDelay(() -> {
            log.info("定时线程池执行: {}", System.currentTimeMillis());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, initDelay, delay, TimeUnit.SECONDS);

        System.in.read();
    }

    @After
    public void after() {
        scheduled.shutdown();
    }
}
