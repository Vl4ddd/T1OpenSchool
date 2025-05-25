package com.academy.taskService.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com.academy.taskService.Dto.TaskDTO;
import com.academy.taskService.Kafka.KafkaTaskProducer;
import com.academy.taskService.Kafka.MessageDeserializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaConfig {
    
    @Value("${task.kafka.consumer.group-id}")
    private String groupId;
    @Value("${task.kafka.bootstrap-servers}")
    private String server;
    @Value("${task.kafka.session.timeout.ms:15000}")
    private String sessionTimeout;
    @Value("${task.kafka.max.partition.fetch.bytes:300000}")
    private String maxPartitionFetchBytes;
    @Value("${task.kafka.max.pool.records:1}")
    private String maxPoolRecords;
    @Value("${task.kafka.max.poll.intervals.ms:3000}")
    private String maxPollIntervalMs;
    @Value("${task.kafka.topic.email_notification}")
    private String topic;

    @Bean
    public ConsumerFactory<String, TaskDTO> consumerListenerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, TaskDTO.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPoolRecords);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);

        DefaultKafkaConsumerFactory factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return factory;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, TaskDTO> kafkaListenerContainerFactory(
            @Qualifier("consumerListenerFactory") ConsumerFactory<String, TaskDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TaskDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerFactory, factory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 1));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners(((record, ex, deliveryAttempt) ->
                log.error("RetryListeners message = {}, offset = {}, deliveryAttempt = {}",
                        ex.getMessage(), record.offset(), deliveryAttempt)));
        return handler;
    }

    @Bean("task")
    public KafkaTemplate<String, TaskDTO> kafkaTemplate(ProducerFactory<String, TaskDTO> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @ConditionalOnProperty(value = "task.kafka.producer.enable",
            havingValue = "true",
            matchIfMissing = true)
    public KafkaTaskProducer producerTask(@Qualifier("task") KafkaTemplate template) {
        template.setDefaultTopic(topic);
        return new KafkaTaskProducer(template);
    }

    @Bean
    public ProducerFactory<String, TaskDTO> producerTaskFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.FALSE);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
