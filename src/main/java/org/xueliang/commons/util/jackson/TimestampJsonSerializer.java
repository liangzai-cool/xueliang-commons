package org.xueliang.commons.util.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * Timestamp 序列化器
 * @author XueLiang
 * @date 2018/10/14 23:50
 */
public class TimestampJsonSerializer extends JsonSerializer<Timestamp> {

    private String pattern = "yyyy-MM-dd HH:mm:ss.SSSZ";

    public TimestampJsonSerializer() {

    }

    public TimestampJsonSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String serializedValue = null;
        if (value != null) {
            serializedValue = value.toLocalDateTime().format(DateTimeFormatter.ofPattern(this.pattern));
        }
        JsonSerializer<Object> jsonSerializer = serializers.findTypedValueSerializer(String.class, true, null);
        jsonSerializer.serialize(serializedValue, gen, serializers);
    }

    @Override
    public Class<Timestamp> handledType() {
        return Timestamp.class;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
