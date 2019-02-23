package org.opentsdb.client;

import lombok.Data;

/**
 * @Description: opentsdb通用配置
 * @Author: jinyao
 * @CreateDate: 2019/2/21 下午9:06
 * @Version: 1.0
 */
@Data
public class OpenTSDBConfig {

    private String host;

    private int port;

    private int httpConnectionPool;

    private int httpConnectTimeout;

    private int putConsumerThreadCount;

    private int batchPutSize;

    private int batchPutBufferSize;

    private int batchPutTimeLimit;

    private boolean readonly;

    public static class Builder {

        private String host;

        private int port;

        /**
         * 每个Host分配的连接数
         */
        private int httpConnectionPool = 100;

        /**
         * 单位：秒
         */
        private int httpConnectTimeout = 100;

        /***
         * 发送数据时，消费者线程
         */
        private int putConsumerThreadCount = 2;

        /**
         * 每个http请求提交的数据大小
         */
        private int batchPutSize = 50;

        /***
         * 生产着消费者模式中，缓冲池的大小
         */
        private int batchPutBufferSize = 20000;

        /***
         * 每次提交等待的最大时间限制，单位ms
         */
        private int batchPutTimeLimit = 300;

        /***
         * 如果确定不写入数据，可以把这个属性设置为true，将不会开启写数据用的队列和线程池
         */
        private boolean readonly = false;

        public Builder(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public OpenTSDBConfig config() {
            OpenTSDBConfig config = new OpenTSDBConfig();

            config.host = this.host;
            config.port = this.port;
            config.httpConnectTimeout = this.httpConnectTimeout;
            config.httpConnectionPool = this.httpConnectionPool;
            config.putConsumerThreadCount = this.putConsumerThreadCount;
            config.batchPutSize = this.batchPutSize;
            config.batchPutBufferSize = this.batchPutBufferSize;
            config.batchPutTimeLimit = this.batchPutTimeLimit;
            config.readonly = this.readonly;

            return config;
        }

        public Builder httpConnectionPool(int connectionPool) {
            if (connectionPool < 1) {
                throw new IllegalArgumentException("The ConnectionPool can't be less then 1");
            }
            httpConnectionPool = connectionPool;
            return this;
        }

        public Builder httpConnectTimeout(int httpConnectTimeout) {
            if (httpConnectTimeout <= 0) {
                throw new IllegalArgumentException("The connectTimtout can't be less then 0");
            }
            this.httpConnectTimeout = httpConnectTimeout;
            return this;
        }

        public Builder putConsumerThreadCount(int putConsumerThreadCount) {
            if (putConsumerThreadCount < 1) {
                throw new IllegalArgumentException("The threadCount can't be less then 1");
            }
            this.putConsumerThreadCount = putConsumerThreadCount;
            return this;
        }

        public Builder batchPutSize(int batchPutSize) {
            if (batchPutSize < 1) {
                throw new IllegalArgumentException("The size can't be less then 1");
            }
            this.batchPutSize = batchPutSize;
            return this;
        }

        public Builder batchPutBufferSize(int batchPutBufferSize) {
            if (batchPutBufferSize < 1) {
                throw new IllegalArgumentException("The size can't be less then 1");
            }
            this.batchPutBufferSize = batchPutBufferSize;
            return this;
        }

        public Builder batchPutTimeLimit(int batchPutTimeLimit) {
            if (batchPutTimeLimit < 1) {
                throw new IllegalArgumentException("The time limit can't be less then 1");
            }
            this.batchPutTimeLimit = batchPutTimeLimit;
            return this;
        }

        public Builder readonly(boolean readonly) {
            this.readonly = readonly;
            return this;
        }

    }

    public static Builder address(String host, int port) {
        return new Builder(host, port);
    }

}
