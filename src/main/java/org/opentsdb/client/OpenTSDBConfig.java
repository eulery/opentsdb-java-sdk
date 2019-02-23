package org.opentsdb.client;

import lombok.Data;

/**
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client
 * @Description: opentsdb通用配置
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:06
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/21 下午9:06
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Data
public class OpenTSDBConfig {

    private String host;

    private int port;

    private int httpConnectionPool = 100; // 每个Host分配的连接数

    private int httpConnectTimeout = 100; // 单位：秒

    public static class Builder {

        private String host;

        private int port;

        private int httpConnectionPool = 100;

        private int httpConnectTimeout = 100;

        public Builder(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public OpenTSDBConfig config() {
            OpenTSDBConfig config = new OpenTSDBConfig();

            config.host = this.host;
            config.port = this.port;

            return config;
        }

        public Builder httpConnectionPool(int connectionPool) {
            if (connectionPool <= 0) {
                throw new IllegalArgumentException("The ConnectionPool con't be less then 1");
            }
            httpConnectionPool = connectionPool;
            return this;
        }

        public Builder httpConnectTimeout(int httpConnectTimeout) {
            if (httpConnectTimeout <= 0) {
                throw new IllegalArgumentException("The connectTimtout con't be less then 0");
            }
            this.httpConnectTimeout = httpConnectTimeout;
            return this;
        }

    }

    public static Builder address(String host, int port) {
        return new Builder(host, port);
    }

}
