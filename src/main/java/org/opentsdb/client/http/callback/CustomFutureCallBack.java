package org.opentsdb.client.http.callback;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义FutureCallBack，用来对任务完成、异常、取消后进行减数
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午10:03
 * @Version: 1.0
 */
@Slf4j
public class CustomFutureCallBack implements FutureCallback<HttpResponse>{

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
