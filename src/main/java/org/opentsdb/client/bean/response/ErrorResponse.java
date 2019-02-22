package org.opentsdb.client.bean.response;

import lombok.Data;

/**
 * 错误信息
 *
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client.bean.response
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:50
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/22 下午7:50
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Data
public class ErrorResponse {

    private Error error;

    @Data
    public static class Error{

        private int status;

        private String message;

    }

}
