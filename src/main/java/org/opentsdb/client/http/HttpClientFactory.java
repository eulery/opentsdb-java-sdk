package org.opentsdb.client.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.opentsdb.client.OpenTSDBConfig;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client.http
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午1:29
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/22 下午1:29
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Slf4j
public class HttpClientFactory {

    private static final AtomicInteger NUM = new AtomicInteger();

    /***
     * 创建httpclient
     * @param config
     * @return
     * @throws IOReactorException
     */
    public static HttpClient createHttpClient(OpenTSDBConfig config) throws IOReactorException {
        Objects.requireNonNull(config);

        ConnectingIOReactor ioReactor = initIOReactorConfig();
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);

        RequestConfig requestConfig = initRequestConfig();
        CloseableHttpAsyncClient httpAsyncClient = createPoolingHttpClient(requestConfig, connManager);

        return new HttpClient(config, httpAsyncClient, initFixedCycleCloseConnection(connManager));
    }

    /***
     * 创建CPU核数的IO线程
     * @return
     * @throws IOReactorException
     */
    private static ConnectingIOReactor initIOReactorConfig() throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                                                         .setIoThreadCount(Runtime.getRuntime()
                                                                                  .availableProcessors())
                                                         .build();
        ConnectingIOReactor ioReactor;
        ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        return ioReactor;
    }

    /***
     * 设置超时时间
     * @return
     */
    private static RequestConfig initRequestConfig() {
        return RequestConfig.custom()
                            .setConnectTimeout(50000)
                            .setSocketTimeout(50000)
                            .setConnectionRequestTimeout(1000)
                            .build();
    }

    /***
     * 创建client
     * @param config
     * @param cm
     * @return
     */
    private static CloseableHttpAsyncClient createPoolingHttpClient(RequestConfig config,
                                                                    PoolingNHttpClientConnectionManager cm) {
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(100);

        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                                                          .setConnectionManager(cm)
                                                          .setDefaultRequestConfig(config)
                                                          .build();
        return client;
    }

    /***
     * 创建定时任务线程池
     * @param cm
     * @return
     */
    private static ScheduledExecutorService initFixedCycleCloseConnection(final PoolingNHttpClientConnectionManager cm) {
        // 通过工厂方法创建线程
        ScheduledExecutorService connectionGcService = Executors.newSingleThreadScheduledExecutor(
                (r) -> {
                    Thread t = new Thread(r, "Fixed-Cycle-Close-Connection-" + NUM.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
        );

        // 定时关闭所有空闲链接
        connectionGcService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    log.debug("Close idle connections, fixed cycle operation");
                    // 关闭30秒内不活动的链接
                    cm.closeExpiredConnections();
                    cm.closeIdleConnections(30, TimeUnit.SECONDS);
                } catch (Exception ex) {
                    log.error("", ex);
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
        return connectionGcService;
    }

}
