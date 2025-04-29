package com.academy.taskService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academy.taskService.Entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
}
