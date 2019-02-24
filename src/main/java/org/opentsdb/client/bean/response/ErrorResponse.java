package org.opentsdb.client.bean.response;

import lombok.Data;

import java.text.MessageFormat;

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

    @Override
    public String toString() {
        return MessageFormat.format(
                "调用OpenTSDB http api发生错误，响应码:{0},错误信息:{1}",
                error.getCode(),
                error.getMessage()
        );
    }

    @Data
    public static class Error {

        private int code;

        private String message;

    }

}
