package org.opentsdb.client.bean.response;

import lombok.Data;
import org.opentsdb.client.bean.request.Point;

import java.util.List;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午8:07
 * @Version: 1.0
 */
@Data
public class DetailResult {

    private List<ErrorPoint> errors;

    private int failed;

    private int success;

    @Data
    public static class ErrorPoint{

        private Point datapoint;

        private String error;

    }

}
