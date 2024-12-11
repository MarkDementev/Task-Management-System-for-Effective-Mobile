package com.tms.service;

import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.model.task.Task;

import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {
    Task getTask(Long id);
    List<Task> getTasks();
    Page<List<Task>> getTasksFiltered(Long userId, String userType, int limit);
    Task createTask(TaskDTO taskDTO);
    Task updateTask(Long id, UpdateTaskAdminDTO updateTaskDTO);
    Task updateTask(Long id, UpdateTaskDTO updateTaskDTO);
    void deleteTask(Long id);
}
