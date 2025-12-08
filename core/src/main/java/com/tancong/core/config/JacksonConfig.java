package com.tancong.core.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * ===================================
 * 这是一个Jaskson序列化配置类
 * ===================================
 *
 * @author tancong
 * @version 1.0.0
 * @create 2025/11/25
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 序列化枚举值为数据库存储值
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
        SimpleModule module = new SimpleModule();
        // 添加时间序列化
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        // 添加时间反序列化
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        // 全局将long转为String
        module.addSerializer(Long.class, ToStringSerializer.instance);
        mapper.registerModule(module);
        return mapper;
    }

    // LocalDateTime序列化
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
    }
    /*上面是要LocalDateTime序列化时间戳，下面是把LocalDateTime序列化一种规范模式*/
    public static class LocalDateTimeStringSerializer extends JsonSerializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                gen.writeString(value.format(FORMATTER));
            }
        }
    }

    // LocalDateTime反序列化
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonParser.getValueAsLong()), ZoneOffset.of("+8"));
        }
    }
    /*上面定义的是时间戳反序列化LocalDateTime，下面是以一种规范模式反序列化LocalDateTime*/
    public static class LocalDateTimeStringDeserializer extends JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.parse(p.getText(), FORMATTER);
        }
    }

}
