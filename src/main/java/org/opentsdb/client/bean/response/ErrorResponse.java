package org.opentsdb.client.bean.response;

import lombok.Data;

/**
 * 错误信息
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:50
 * @Version: 1.0
 */
@Data
public class ErrorResponse {

    private Error error;

    @Data
    public static class Error{

        private int code;

        private String message;

    }

}
