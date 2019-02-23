package org.opentsdb.client;

import org.apache.http.nio.reactor.IOReactorException;

/**
 * @Description: openstadb客户端工厂类
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:13
 * @Version: 1.0
 */
public class OpenTSDBClientFactory {

    public static OpenTSDBClient connect(OpenTSDBConfig config) throws IOReactorException {
        return new OpenTSDBClient(config);
    }

}
