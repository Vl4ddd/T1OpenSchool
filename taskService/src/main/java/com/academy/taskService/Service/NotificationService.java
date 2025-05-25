package com.academy.taskService.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.academy.taskService.Dto.TaskDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String EMAIL_SUBJECT = "Обновление статуса у задачи";

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.myusername}")
    private String myEmail;

    public void sendEmailNotification(List<TaskDTO> tasks) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmail);
        simpleMailMessage.setTo(myEmail);
        simpleMailMessage.setSubject(EMAIL_SUBJECT);
        StringBuilder text = new StringBuilder();
        for (TaskDTO task : tasks) {
            text.append("Задача с ID=").append(task.getId())
                    .append(" изменена, новый статус: ").append(task.getStatus())
                    .append(System.lineSeparator());
        }
        simpleMailMessage.setText(text.toString());
        emailSender.send(simpleMailMessage);
    }
}
