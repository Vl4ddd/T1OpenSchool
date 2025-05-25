package com.academy.taskService.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import com.academy.taskService.Entity.TaskStatus;

import lombok.Data;

@Data
public class TaskDTO {

    @Schema(description = "ID (не указывать при создании)", accessMode = READ_ONLY)
    private Long id;
    private String title;
    private String description;
    private Long userId;
     private TaskStatus status;
}
