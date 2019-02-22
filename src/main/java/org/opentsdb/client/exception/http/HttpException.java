package org.opentsdb.client.exception.http;

import lombok.Data;
import org.opentsdb.client.bean.response.ErrorResponse;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client.exception.http
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:40
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/22 下午7:40
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
@Data
public class HttpException extends RuntimeException {

    private ErrorResponse.Error error;

    public HttpException(ErrorResponse errorResponse) {
        super();
        ErrorResponse.Error error = errorResponse.getError();
        Objects.requireNonNull(error);
        this.error = error;
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "调用OpenTSDB http api发生错误，响应码:{0},错误信息:{1}",
                error.getStatus(),
                error.getMessage()
        );
    }
}
