# opentsdb-java-sdk
[http api地址](http://opentsdb.net/docs/build/html/api_http/index.html#api-endpoints)

# 目前实现的功能
* 查询数据，支持同步和异步
* 写入数据，支持异步回调
* 删除数据
* 查询最新数据
* 查询metric、tag_key和tag_value，支持auto_complete
<br>
`源码中CrudTest类提供了一些使用说明和测试，包括并发查询测试和并发写入测试`

# 快速开始
## Maven依赖
```xml
<dependency>
    <groupId>com.github.eulery</groupId>
    <artifactId>opentsdb-java-sdk</artifactId>
    <version>1.1.4</version>
</dependency>
```
## 创建连接
```java
OpenTSDBConfig config = OpenTSDBConfig.address(host, port)
                                              .config();
OpenTSDBClient client = OpenTSDBClientFactory.connect(config);

// 优雅关闭连接，会等待所有异步操作完成
client.gracefulClose();
```
```java
OpenTSDBConfig.address(host, port)
              // http连接池大小，默认100
              .httpConnectionPool(100)
              // http请求超时时间，默认100s
              .httpConnectTimeout(100)
              // 异步写入数据时，每次http提交的数据条数，默认50
              .batchPutSize(50)
              // 异步写入数据中，内部有一个队列，默认队列大小20000
              .batchPutBufferSize(20000)
              // 异步写入等待时间，如果距离上一次请求超多300ms，且有数据，则直接提交
              .batchPutTimeLimit(300)
              // 当确认这个client只用于查询时设置，可不创建内部队列从而提高效率
              .readonly()
              // 每批数据提交完成后回调
              .batchPutCallBack(new BatchPutHttpResponseCallback.BatchPutCallBack() {
                  @Override
                  public void response(List<Point> points, DetailResult result) {
                      // 在请求完成并且response code成功时回调
                  }

                  @Override
                  public void responseError(List<Point> points, DetailResult result) {
                      // 在response code失败时回调
                  }

                  @Override
                  public void failed(List<Point> points, Exception e) {
                      // 在发生错误是回调
                  }
              })

```
## 查询数据 
具体参数使用说明查看[http api](http://opentsdb.net/docs/build/html/api_http/query/index.html)
```java
Query query = Query.begin("7d-ago")
                   .sub(SubQuery.metric("metric.test")
                                .aggregator(SubQuery.Aggregator.NONE)
                                .build())
                   .build();
// 同步查询
List<QueryResult> resultList = client.query(query);

// 异步查询
client.query(query, new QueryHttpResponseCallback.QueryCallback() {
    @Override
    public void response(Query query, List<QueryResult> queryResults) {
        // 在请求完成并且response code成功时回调
    }

    @Override
    public void responseError(Query query, HttpException e) {
        // 在response code失败时回调
    }

    @Override
    public void failed(Query query, Exception e) {
        // 在发生错误是回调
    }
});
```
## 写入数据
具体参数使用说明查看[http api](http://opentsdb.net/docs/build/html/api_http/put.html)
```java
Point point = Point.metric("point")
                  .tag("testTag", "test")
                  .value(timestamp, 1.0)
                  .build();
client.put(point);
```
## 查询最新数据
具体参数使用说明查看[http api](http://opentsdb.net/docs/build/html/api_http/query/last.html)
```java
LastPointQuery query = LastPointQuery.sub(LastPointSubQuery.metric("point")
                                                           .tag("testTag", "test_1")
                                                           .build())
                                     // baskScan表示查询最多向前推进多少小时
                                     // 比如在5小时前写入过数据
                                     // 那么backScan(6)可以查出数据，但backScan(4)则不行
                                     .backScan(1000)
                                     .build();
List<LastPointQueryResult> lastPointQueryResults = client.queryLast(query);
```

## 删除数据
删除数据和查询数据使用同一个Query，查询命中的数据将会被删除
```java
Query query = Query.begin("7d-ago")
                   .sub(SubQuery.metric("metric.test")
                                .aggregator(SubQuery.Aggregator.NONE)
                                .build())
                   .build();
client.delete(query);
```
## 查询metrics、tagk、tagv
具体参数使用说明查看[http api](http://opentsdb.net/docs/build/html/api_http/suggest.html)
```java
SuggestQuery query = SuggestQuery.type(SuggestQuery.Type.METRICS)
                                 .build();
List<String> suggests = client.querySuggest(query);
```

## ttl
如果想为数据设置ttl(time to live)，opentsdb没有直接提供这方面的api，只能通过底层hbase的ttl来完成‘’
```shell
hbase> describe 'tsdb'
Table tsdb is ENABLED
tsdb, {NAME => 't', VERSIONS => 1, COMPRESSION => 'NONE', TTL => 'FOREVER'}

hbase> alter ‘tsdb′, NAME => ‘t′, TTL => 8640000
```

