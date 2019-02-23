package org.opentsdb.client.bean.response;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 上午11:51
 * @Version: 1.0
 */
@Data
public class QueryResult {

    private String metric;

    private Map<String, String> tags;

    private List<String> aggregateTags;

    private LinkedHashMap<Long, Number> dps;

}
