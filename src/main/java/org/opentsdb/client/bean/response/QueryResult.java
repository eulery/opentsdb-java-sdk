package org.opentsdb.client.bean.response;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client.bean.response
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 上午11:51
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/22 上午11:51
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Data
public class QueryResult {

    private String metric;

    private Map<String, String> tags;

    private List<String> aggregateTags;

    private LinkedHashMap<Long, Number> dps;

}
