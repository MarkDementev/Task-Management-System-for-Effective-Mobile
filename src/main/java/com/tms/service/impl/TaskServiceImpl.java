package com.tms.service.impl;

import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.exception.EntityWithIDNotFoundException;
import com.tms.model.task.Task;
import com.tms.model.user.Admin;
import com.tms.model.user.User;
import com.tms.repository.TaskRepository;
import com.tms.service.AdminService;
import com.tms.service.TaskService;
import com.tms.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final AdminService adminService;

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDTO taskDTO) {
        Admin admin = adminService.getAdmin();
        Task taskToCreate;

        if (taskDTO.getExecutionerID() == null) {
            taskToCreate = new Task(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getTaskPriority());
        } else {
            User executioner = userService.getUser(taskDTO.getExecutionerID());
            taskToCreate = new Task(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getTaskPriority(),
                    executioner);
        }
        taskToCreate.setAuthor(admin);

        AtomicReference<Task> atomicNewTask = new AtomicReference<>(taskToCreate);

        return taskRepository.save(atomicNewTask.get());
    }

    @Override
    public Task updateTask(Long id, UpdateTaskAdminDTO updateTaskDTO) {
        AtomicReference<Task> atomicTaskToUpdate = new AtomicReference<>(
                taskRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        Task.class.getSimpleName(), id))
        );
        User executioner = null;

        if (updateTaskDTO.getExecutionerID() != null) {
            executioner = userService.getUser(updateTaskDTO.getExecutionerID().get());
        }

        if (updateTaskDTO.getTitle() != null) {
            atomicTaskToUpdate.get().setTitle(updateTaskDTO.getTitle().get());
        }
        if (updateTaskDTO.getDescription() != null) {
            atomicTaskToUpdate.get().setDescription(updateTaskDTO.getDescription().get());
        }
        if (updateTaskDTO.getTaskPriority() != null) {
            atomicTaskToUpdate.get().setTaskPriority(updateTaskDTO.getTaskPriority().get());
        }
        if (executioner != null) {
            atomicTaskToUpdate.get().setExecutioner(executioner);
        }
        if (updateTaskDTO.getTaskStatus() != null) {
            atomicTaskToUpdate.get().setTaskStatus(updateTaskDTO.getTaskStatus().get());
        }
        return taskRepository.save(atomicTaskToUpdate.get());
    }

    @Override
    public Task updateTask(Long id, UpdateTaskDTO updateTaskDTO) {
        AtomicReference<Task> atomicTaskToUpdate = new AtomicReference<>(
                taskRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        Task.class.getSimpleName(), id))
        );

        if (updateTaskDTO.getTaskStatus() != null) {
            atomicTaskToUpdate.get().setTaskStatus(updateTaskDTO.getTaskStatus().get());
        }
        return taskRepository.save(atomicTaskToUpdate.get());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
