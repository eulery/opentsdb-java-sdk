package org.opentsdb.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.nio.reactor.IOReactorException;
import org.opentsdb.client.bean.request.Api;
import org.opentsdb.client.bean.request.LastPointQuery;
import org.opentsdb.client.bean.request.Point;
import org.opentsdb.client.bean.request.Query;
import org.opentsdb.client.bean.response.LastPointQueryResult;
import org.opentsdb.client.bean.response.QueryResult;
import org.opentsdb.client.common.Json;
import org.opentsdb.client.http.HttpClient;
import org.opentsdb.client.http.HttpClientFactory;
import org.opentsdb.client.http.callback.QueryHttpResponseCallback;
import org.opentsdb.client.sender.consumer.Consumer;
import org.opentsdb.client.sender.consumer.ConsumerImpl;
import org.opentsdb.client.sender.producer.Producer;
import org.opentsdb.client.sender.producer.ProducerImpl;
import org.opentsdb.client.util.ResponseUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description: opentsdb客户端
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:16
 * @Version: 1.0
 */
@Slf4j
public class OpenTSDBClient {

    private final OpenTSDBConfig config;

    private final HttpClient httpClient;

    private Producer producer;

    private Consumer consumer;

    private BlockingQueue<Point> queue;

    /***
     * 通过反射来允许删除
     */
    private static Field queryDeleteField;

    public OpenTSDBClient(OpenTSDBConfig config) throws IOReactorException {
        this.config = config;
        this.httpClient = HttpClientFactory.createHttpClient(config);
        this.httpClient.start();

        if (!config.isReadonly()) {
            this.queue = new ArrayBlockingQueue<>(config.getBatchPutBufferSize());
            this.producer = new ProducerImpl(queue);
            this.consumer = new ConsumerImpl(queue, httpClient, config);
            this.consumer.start();

            try {
                queryDeleteField = Query.class.getDeclaredField("delete");
                queryDeleteField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        log.debug("the httpclient has started");
    }

    /***
     * 查询数据
     * @param query
     * @return
     */
    public List<QueryResult> query(Query query) throws IOException, ExecutionException, InterruptedException {
        Future<HttpResponse> future = httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query));
        HttpResponse response = future.get();
        List<QueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, QueryResult.class);
        return results;
    }

    /***
     * 异步查询
     * @param query
     * @param callback
     */
    public void query(Query query, QueryHttpResponseCallback.QueryCallback callback) throws JsonProcessingException {
        QueryHttpResponseCallback queryHttpResponseCallback = new QueryHttpResponseCallback(callback, query);
        httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query), queryHttpResponseCallback);
    }

    /***
     * 查询最新的数据
     * @param query
     * @return
     */
    public List<LastPointQueryResult> queryLast(LastPointQuery query) throws IOException, ExecutionException, InterruptedException {
        Future<HttpResponse> future = httpClient.post(Api.LAST.getPath(), Json.writeValueAsString(query));
        HttpResponse response = future.get();
        List<LastPointQueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, LastPointQueryResult.class);
        return results;
    }

    /***
     * 写入数据
     * @param point
     */
    public void put(Point point) {
        if (config.isReadonly()) {
            throw new IllegalArgumentException("this client is readonly,can't put point");
        }
        producer.send(point);
    }

    /***
     * 删除数据，返回删除的数据
     * @param query
     */
    public List<QueryResult> delete(Query query) throws IllegalAccessException, ExecutionException, InterruptedException, IOException {
        if (config.isReadonly()) {
            throw new IllegalArgumentException("this client is readonly,can't delete data");
        }
        queryDeleteField.set(query, true);
        Future<HttpResponse> future = httpClient.post(Api.QUERY.getPath(), Json.writeValueAsString(query));
        HttpResponse response = future.get();
        List<QueryResult> results = Json.readValue(ResponseUtil.getContent(response), List.class, QueryResult.class);
        return results;
    }

    /***
     * 优雅关闭链接，会等待所有消费者线程结束
     */
    public void gracefulClose() throws IOException {
        if (!config.isReadonly()) {
            // 先停止写入
            this.producer.forbiddenSend();
            // 等待队列被消费空
            this.waitEmpty();
            // 关闭消费者
            this.consumer.gracefulStop();
        }
        this.httpClient.gracefulClose();
    }

    /***
     * 等待队列被消费空
     */
    private void waitEmpty() {
        while (!queue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(config.getBatchPutTimeLimit());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * 强行关闭
     */
    public void forceClose() throws IOException {
        if (!config.isReadonly()) {
            this.consumer.forceStop();
        }
        this.httpClient.forceClose();
    }

}
