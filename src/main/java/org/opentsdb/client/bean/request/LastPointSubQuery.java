package org.opentsdb.client.bean.request;

import lombok.Data;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午2:48
 * @Version: 1.0
 */
@Data
public class LastPointSubQuery {

    private String metric;

    private Map<String, String> tags;

    public static class Builder {

        private String metric;

        private Map<String, String> tags = new HashMap<>();

        public LastPointSubQuery build() {
            LastPointSubQuery query = new LastPointSubQuery();
            query.metric = this.metric;
            query.tags = this.tags;
            return query;
        }

        public Builder(String metric) {
            this.metric = metric;
        }

        public Builder tag(String tagk, String tagv) {
            if (StringUtils.isNoneBlank(tagk) && StringUtils.isNoneBlank(tagv)) {
                this.tags.put(tagk, tagv);
            }
            return this;
        }

        public Builder tag(Map<String, String> tags) {
            if (!MapUtils.isEmpty(tags)) {
                this.tags.putAll(tags);
            }
            return this;
        }

    }

    public static Builder metric(String metric) {
        return new Builder(metric);
    }

}
