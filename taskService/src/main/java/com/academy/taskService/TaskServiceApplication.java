package com.academy.taskService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class TaskServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskServiceApplication.class, args);
	}

}
