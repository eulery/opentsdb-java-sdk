package org.opentsdb.client.bean.response;

import lombok.Data;

import java.util.Map;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午3:07
 * @Version: 1.0
 */
@Data
public class LastPointQueryResult {

    private String metric;

    private long timestamp;

    private Object value;

    private String tsuid;

    private Map<String, String> tags;

}
