package com.academy.taskService.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academy.taskService.Dto.TaskDTO;
import com.academy.taskService.Entity.Task;
import com.academy.taskService.Repository.TaskRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelMapper mapper;

    public TaskDTO getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        TaskDTO taskDto = mapper.map(task, TaskDTO.class);
        return taskDto;
    }

    public List<TaskDTO> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(task -> mapper.map(task, TaskDTO.class))
                .collect(Collectors.toList());
    }

    public TaskDTO createTask(TaskDTO taskDto) {
        Task task = mapper.map(taskDto, Task.class);
        Task savedTask = taskRepository.save(task);
        TaskDTO savedTaskDto = mapper.map(savedTask, TaskDTO.class);
        return savedTaskDto;
    }

    public TaskDTO deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена: " + id));
        TaskDTO deletedTaskDto = mapper.map(task, TaskDTO.class);
        taskRepository.delete(task);
        return deletedTaskDto;
    }

    public TaskDTO updateTask(Long id, TaskDTO taskDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача не найдена: " + id));
        taskRepository.delete(task);
        Task newTask = mapper.map(taskDto, Task.class);
        taskRepository.save(newTask);

        return taskDto;
    }

}
