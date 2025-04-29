package com.academy.taskService.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Long userId;
}
