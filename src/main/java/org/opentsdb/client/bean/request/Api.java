package org.opentsdb.client.bean.request;

/**
 * api地址
 *
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client.bean.request
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/23 下午12:49
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/23 下午12:49
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public enum Api {

    /***
     * path对应api地址
     */
    PUT("/api/put"),
    QUERY("/api/query");

    private String path;

    Api(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
