package org.opentsdb.client;

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
public class OpenTSDBConfig {

    private String host;

    private int port;

    public static class Builder {

        private String host;

        private int port;

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

    }

    public static Builder address(String host, int port) {
        return new Builder(host, port);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
