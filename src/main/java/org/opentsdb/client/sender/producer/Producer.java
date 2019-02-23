package org.opentsdb.client.sender.producer;

import org.opentsdb.client.bean.request.Point;

/**
 * 生产者接口
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:07
 * @Version: 1.0
 */
public interface Producer {

    /***
     * 写入队列
     * @param point
     */
    void send(Point point);

    /***
     * 关闭写入
     */
    void forbiddenSend();

}
