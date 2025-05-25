package com.academy.taskService.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academy.taskService.Aspect.Annotation.ExceptionHandling;
import com.academy.taskService.Aspect.Annotation.LogTracking;
import com.academy.taskService.Aspect.Annotation.LogUpdate;
import com.academy.taskService.Aspect.Annotation.Loggable;
import com.academy.taskService.Dto.TaskDTO;
import com.academy.taskService.Entity.Task;
import com.academy.taskService.Repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final ModelMapper mapper;

    @LogTracking
    @Loggable
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена: " + id));
        TaskDTO taskDto = mapper.map(task, TaskDTO.class);
        return taskDto;
    }

    @LogTracking
    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDto) {
        Task task = mapper.map(taskDto, Task.class);
        task.setId(null);
        Task savedTask = taskRepository.save(task);
        TaskDTO savedTaskDto = mapper.map(savedTask, TaskDTO.class);
        return savedTaskDto;
    }

    @ExceptionHandling
    public TaskDTO deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена: " + id));
        TaskDTO deletedTaskDto = mapper.map(task, TaskDTO.class);
        taskRepository.delete(task);
        return deletedTaskDto;
    }

    @LogUpdate
    public TaskDTO updateTask(Long id, TaskDTO taskDto) {
        Task existingTask = taskRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Задача не найдена: " + id));
        mapper.map(taskDto, existingTask);
        existingTask.setId(id); 
        Task updatedTask = taskRepository.save(existingTask);
        return mapper.map(updatedTask, TaskDTO.class);
    }

}
