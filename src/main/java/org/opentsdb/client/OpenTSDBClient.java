package org.opentsdb.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.nio.reactor.IOReactorException;
import org.opentsdb.client.bean.request.Query;
import org.opentsdb.client.bean.response.QueryResult;
import org.opentsdb.client.http.HttpClient;
import org.opentsdb.client.http.HttpClientFactory;
import org.opentsdb.client.util.Json;
import org.opentsdb.client.util.ResponseUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client
 * @Description: opentsdb客户端
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:16
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/21 下午9:16
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Slf4j
public class OpenTSDBClient {

    private final OpenTSDBConfig config;

    private final HttpClient httpClient;

    public OpenTSDBClient(OpenTSDBConfig config) throws IOReactorException {
        this.config = config;
        this.httpClient = HttpClientFactory.createHttpClient(config);
        this.httpClient.start();
        log.debug("the httpclient has started");
    }

    /***
     * 查询数据
     * @param query
     * @return
     */
    public List<QueryResult> query(Query query) throws IOException, ExecutionException, InterruptedException {
        ObjectMapper mapper = Json.getInstance();
        HttpResponse response = httpClient.post("/api/query", mapper.writeValueAsString(query));
        CollectionType collectionType = mapper.getTypeFactory()
                                              .constructCollectionType(List.class, QueryResult.class);
        List<QueryResult> results = mapper.readValue(ResponseUtil.getContent(response), collectionType);
        return results;
    }



}
