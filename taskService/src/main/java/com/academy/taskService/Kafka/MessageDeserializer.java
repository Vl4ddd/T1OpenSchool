package com.academy.taskService.Kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception ex) {
            log.warn("Deserialization message error {}", new String(data, StandardCharsets.UTF_8), ex);
            return null;
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception ex) {
            log.warn("Deserialization message error {}", new String(data, StandardCharsets.UTF_8), ex);
            return null;
        }
    }
}
