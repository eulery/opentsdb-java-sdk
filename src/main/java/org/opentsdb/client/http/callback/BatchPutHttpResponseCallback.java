package org.opentsdb.client.http.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * 实现put后的回调
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午9:58
 * @Version: 1.0
 */
@Slf4j
public class BatchPutHttpResponseCallback implements FutureCallback<HttpResponse> {

    @Override
    public void completed(HttpResponse response) {

    }

    @Override
    public void failed(Exception e) {

    }

    @Override
    public void cancelled() {

    }
}
