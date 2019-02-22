package org.opentsdb.client.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.text.SimpleDateFormat;

/**
 *
 * 配置一个通用的jackson objectmapper
 *
 * @ProjectName: javaclient
 * @Package: org.opentsdb.client
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午1:21
 * @UpdateUser: jinyao
 * @UpdateDate: 2019/2/22 下午1:21
 * @UpdateRemark: The modified content
 * @Version: 1.0
 */
public class Json {

    private static final ObjectMapper instance;

    static {
        instance = new ObjectMapper();

        // 支持java8
        instance.registerModule(new JavaTimeModule())
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module());
        instance.findAndRegisterModules();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        instance.setDateFormat(dateFormat);
        // 允许对象忽略json中不存在的属性
        instance.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许出现特殊字符和转义符
        instance.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许出现单引号
        instance.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        // 忽视为空的属性
        instance.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    }

    public static ObjectMapper getInstance(){
        return instance;
    }

}
