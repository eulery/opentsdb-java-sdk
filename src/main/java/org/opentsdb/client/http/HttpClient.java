package org.opentsdb.client.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.opentsdb.client.OpenTSDBConfig;

import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
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
public class HttpClient {

    private String host;

    private int port;

    /**
     * 通过这个client来完成请求
     */
    private final CloseableHttpAsyncClient client;

    /**
     * 未完成任务数 for graceful close.
     */
    private final AtomicInteger unCompletedTaskNum;

    /**
     * 空闲连接清理服务
     */
    private ScheduledExecutorService connectionGcService;

    HttpClient(OpenTSDBConfig config, CloseableHttpAsyncClient client, ScheduledExecutorService connectionGcService) {
        this.host = config.getHost();
        this.port = config.getPort();
        this.client = client;
        this.connectionGcService = connectionGcService;
        this.unCompletedTaskNum = new AtomicInteger(0);
    }

    /***
     * post请求
     * @param path
     * @param json
     * @return
     */
    public HttpResponse post(String path, String json) throws ExecutionException, InterruptedException {
        return this.post(path, json, null);
    }

    /***
     * post请求
     * @param path
     * @param json 请求内容，json格式z
     * @param httpCallback
     * @return
     */
    public HttpResponse post(String path, String json, FutureCallback<HttpResponse> httpCallback) throws ExecutionException, InterruptedException {
        log.debug("发送post请求，路径:{}，请求内容:{}", path, json);
        HttpPost httpPost = new HttpPost(getUrl(path));
        if (StringUtils.isNoneBlank(json)) {
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.setEntity(generateStringEntity(json));
        }

        FutureCallback<HttpResponse> responseCallback = null;
        log.debug("等待完成的任务数:{}", unCompletedTaskNum.incrementAndGet());
        if (httpCallback != null) {
            responseCallback = new CustomFutureCallBack(unCompletedTaskNum, httpCallback);
        }

        Future<HttpResponse> future = client.execute(httpPost, responseCallback);
        HttpResponse httpResponse = null;
        try {
            httpResponse = future.get();
        } finally {
            if (responseCallback == null) {
                log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
            }
        }
        return httpResponse;
    }

    private String getUrl(String path) {
        return host + ":" + port + path;
    }

    private StringEntity generateStringEntity(String json) {
        StringEntity stringEntity = new StringEntity(json, Charset.forName("UTF-8"));
        return stringEntity;
    }

    /***
     * 自定义FutureCallBack，用来对任务完成、异常、取消后进行减数
     */
    public static class CustomFutureCallBack implements FutureCallback<HttpResponse> {

        private final AtomicInteger unCompletedTaskNum;
        private final FutureCallback<HttpResponse> futureCallback;

        public CustomFutureCallBack(AtomicInteger unCompletedTaskNum, FutureCallback<HttpResponse> futureCallback) {
            super();
            this.unCompletedTaskNum = unCompletedTaskNum;
            this.futureCallback = futureCallback;
        }

        @Override
        public void completed(HttpResponse result) {
            futureCallback.completed(result);
            // 任务处理完毕，再减数
            log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
        }

        @Override
        public void failed(Exception ex) {
            futureCallback.failed(ex);
            // 任务处理完毕，再减数
            log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
        }

        @Override
        public void cancelled() {
            futureCallback.cancelled();
            // 任务处理完毕，再减数
            log.debug("等待完成的任务数:{}", unCompletedTaskNum.decrementAndGet());
        }
    }

    public void start(){
        this.client.start();
    }

}
