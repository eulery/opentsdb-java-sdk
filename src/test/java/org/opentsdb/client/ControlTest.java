package org.opentsdb.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.nio.reactor.IOReactorException;
import org.junit.Before;
import org.junit.Test;
import org.opentsdb.client.bean.request.SuggestQuery;

import java.util.List;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/3/9 下午3:18
 * @Version: 1.0
 */
@Slf4j
public class ControlTest {

    String host = "http://127.0.0.1";

    int port = 4242;

    OpenTSDBClient client;

    @Before
    public void config() throws IOReactorException {
        OpenTSDBConfig config = OpenTSDBConfig.address(host, port)
                                              .config();
        OpenTSDBConfig.address(host, port)
                      // 当确认这个client只用于查询时设置，可不创建内部队列从而提高效率
                      .readonly()
                      // 每批数据提交完成后回调
                      .config();
        client = OpenTSDBClientFactory.connect(config);
    }

    @Test
    public void suggestQuery() throws Exception {
        SuggestQuery query = SuggestQuery.type(SuggestQuery.Type.METRICS)
                                         .build();
        List<String> suggests = client.querySuggest(query);
        log.debug("suggest query result:{}", suggests);
    }

}
