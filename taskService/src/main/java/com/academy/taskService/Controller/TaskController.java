package com.academy.taskService.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academy.taskService.Dto.TaskDTO;
import com.academy.taskService.Service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    private TaskDTO getTask(@PathVariable("id") Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(tasks);
        }
    }

    @PostMapping
    private TaskDTO createTask(@RequestBody TaskDTO taskDto) {
        return taskService.createTask(taskDto);
    }

    @DeleteMapping("/{id}")
    private TaskDTO deleteTaskById(@PathVariable("id") Long id) {
        return taskService.deleteTask(id);
    }

    @PutMapping("/{id}")
    private TaskDTO updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDto) {
        return taskService.updateTask(id, taskDto);
    }

}
