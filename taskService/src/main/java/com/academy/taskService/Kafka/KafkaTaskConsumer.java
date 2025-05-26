package com.academy.taskService.Kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.academy.taskService.Dto.TaskDTO;
import com.academy.taskService.Service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaTaskConsumer {

    private final NotificationService notificationService;

    @KafkaListener(id = "${task.kafka.consumer.group-id}",
            topics = "${task.kafka.topic.updating}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload List<TaskDTO> tasks,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Task consumer: processing new messages");
        try {
            notificationService.sendEmailNotification(tasks);
        } finally {
            ack.acknowledge();
        }
        log.info("Task consumer: processed messages");
    }
}