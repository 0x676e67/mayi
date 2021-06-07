package com.zf1976.mayi.common.core.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

/**
 * @author mac
 * @date 2021/5/30
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        // 序列化枚举值
        ObjectMapper objectMapper = builder.featuresToEnable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                                           .createXmlMapper(false)
                                           .build();
        objectMapper.getSerializerProvider()
                    .setNullValueSerializer(new JsonSerializer<>() {
                        @Override
                        public void serialize(Object o,
                                              JsonGenerator jsonGenerator,
                                              SerializerProvider serializerProvider) throws IOException {
                            jsonGenerator.writeString("");
                        }
                    });
        return objectMapper;
    }
}
