package com.tms.service.impl;

import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.exception.EntityWithIDNotFoundException;
import com.tms.model.task.Task;
import com.tms.model.user.Admin;
import com.tms.repository.TaskRepository;
import com.tms.service.AdminService;
import com.tms.service.TaskService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    TaskRepository taskRepository;
    AdminService adminService;

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(TaskDTO taskDTO) {
        Admin admin = adminService.getAdmin();
        Task taskToCreate;

        if (taskDTO.getExecutioner() == null) {
            taskToCreate = new Task(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getTaskPriority());
        } else {
            taskToCreate = new Task(taskDTO.getTitle(), taskDTO.getDescription(), taskDTO.getTaskPriority(),
                    taskDTO.getExecutioner());
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

        atomicTaskToUpdate.get().setTitle(updateTaskDTO.getTitle().get());
        atomicTaskToUpdate.get().setDescription(updateTaskDTO.getDescription().get());
        atomicTaskToUpdate.get().setTaskPriority(updateTaskDTO.getTaskPriority().get());
        atomicTaskToUpdate.get().setExecutioner(updateTaskDTO.getExecutioner().get());
        atomicTaskToUpdate.get().setTaskStatus(updateTaskDTO.getTaskStatus().get());
        return taskRepository.save(atomicTaskToUpdate.get());
    }

    @Override
    public Task updateTask(Long id, UpdateTaskDTO updateTaskDTO) {
        AtomicReference<Task> atomicTaskToUpdate = new AtomicReference<>(
                taskRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                        Task.class.getSimpleName(), id))
        );

        atomicTaskToUpdate.get().setTaskStatus(updateTaskDTO.getTaskStatus().get());
        return taskRepository.save(atomicTaskToUpdate.get());
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
