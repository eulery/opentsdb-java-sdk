package org.opentsdb.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.nio.reactor.IOReactorException;
import org.junit.Before;
import org.junit.Test;
import org.opentsdb.client.bean.request.Point;
import org.opentsdb.client.bean.request.Query;
import org.opentsdb.client.bean.request.SubQuery;
import org.opentsdb.client.bean.response.QueryResult;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午3:53
 * @Version: 1.0
 */
@Slf4j
public class CurdTest {

    String host = "http://127.0.0.1";

    int port = 4242;

    OpenTSDBClient client;

    @Before
    public void config() throws IOReactorException {
        OpenTSDBConfig config = OpenTSDBConfig.address(host, port)
                                              .config();
        client = OpenTSDBClientFactory.connect(config);
    }

    /***
     * 单点查询测试
     */
    @Test
    public void testQuery() throws InterruptedException, ExecutionException, IOException {
        log.debug("当前线程:{}", Thread.currentThread()
                                   .getName());
        Query query = Query.begin("7d-ago")
                           .sub(SubQuery.metric("metric.test")
                                        .aggregator(SubQuery.Aggregator.NONE)
                                        .build())
                           .build();
        List<QueryResult> resultList = client.query(query);
        log.debug("result:{}", resultList);
    }

    /***
     * 并发查询测试
     * 测试结果大概190个线程会出现http超时
     * 之后会把http超时做成参数
     *
     * 更新：已解决这个问题，目前默认超时时间100秒，并可以通过参数改变
     */
    @Test
    public void testQueryConcurrent() {
        CountDownLatch latch = new CountDownLatch(1);
        int threadCount = 2000;
        long[] times = new long[3];
        // 利用CyclicBarrier模拟并发
        CyclicBarrier startBarrier = new CyclicBarrier(
                threadCount,
                () -> {
                    log.debug("所有线程已经就位");
                    times[0] = System.currentTimeMillis();
                }
        );

        CyclicBarrier endBarrier = new CyclicBarrier(
                threadCount,
                () -> {
                    log.debug("所有线程已经执行完毕");
                    times[1] = System.currentTimeMillis();
                    log.debug("运行时间:{}毫秒", times[1] - times[0]);
                    latch.countDown();
                }
        );


        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(
                threadCount,
                (runnable) -> {
                    Thread thread = new Thread(runnable, "thread-" + times[2]++);
                    return thread;
                }
        );
        while (threadCount-- > 0) {
            fixedThreadPool.execute(() -> {
                try {
                    startBarrier.await();
                    // 查询
                    testQuery();
                    endBarrier.await();
                } catch (Exception e) {
                    log.error("", e);
                    e.printStackTrace();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试写入数据
     */
    @Test
    public void put() throws Exception {
        Point point = Point.metric("metric.test")
                           .tag("test", "hello")
                           .value(System.currentTimeMillis(), 1.0)
                           .build();
        client.put(point);
        client.gracefulClose();
    }

    /***
     * 并发写入测试
     */
    @Test
    public void batchPut() throws Exception {
        /***
         * 使用5个线程，每个线程都写入10000条数据
         */
        int threadCount = 5;
        int dataCount = 10000;
        CountDownLatch latch = new CountDownLatch(5);
        int[] ints = new int[1];
        ExecutorService threadPool = Executors.newFixedThreadPool(5, (r) ->
                new Thread(r, String.valueOf(++ints[0]))
        );
        long start = System.currentTimeMillis();

        /**
         * 前一天
         */
        long begin = start - (long)24 * 60 * 60 *1000;
        for (int a = 0; a < threadCount; a++) {
            threadPool.execute(() -> {
                for (int i = 0; i < dataCount; i++) {
                    Point point = Point.metric("metric.test" + Thread.currentThread()
                                                                     .getName())
                                       .tag("test", "hello")
                                       /**
                                        * 每秒一条数据
                                        */
                                       .value(begin + i * 1000, i)
                                       .build();
                    client.put(point);
                }
                latch.countDown();
            });
        }

        latch.await();
        long end = System.currentTimeMillis();
        log.debug("运行时间:{}毫秒", end - start);
        client.gracefulClose();
    }

}
