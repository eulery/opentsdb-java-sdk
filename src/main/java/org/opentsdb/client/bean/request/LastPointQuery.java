package org.opentsdb.client.bean.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午2:48
 * @Version: 1.0
 */
@Data
public class LastPointQuery {

    private List<LastPointSubQuery> queries;

    private boolean resolveNames;

    private int backScan;

    public static class Builder {

        private List<LastPointSubQuery> queries = new ArrayList<>();

        private boolean resolveNames = true;

        private int backScan;

        public LastPointQuery build() {
            LastPointQuery query = new LastPointQuery();
            query.queries = this.queries;
            query.resolveNames = this.resolveNames;
            query.backScan = this.backScan;
            return query;
        }

        public Builder(LastPointSubQuery query) {
            this.queries.add(query);
        }

        public Builder(List<LastPointSubQuery> queries) {
            this.queries = queries;
        }

        public Builder resolveNames(boolean resolveNames) {
            this.resolveNames = resolveNames;
            return this;
        }

        public Builder backScan(int backScan) {
            this.backScan = backScan;
            return this;
        }

    }

    public static Builder sub(LastPointSubQuery query) {
        return new Builder(query);
    }

    public static Builder sub(List<LastPointSubQuery> queries) {
        return new Builder(queries);
    }

}
