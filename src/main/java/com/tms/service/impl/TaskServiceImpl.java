package com.tms.service.impl;

import com.tms.dto.CommentDTO;
import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.exception.EntityWithIDNotFoundException;
import com.tms.model.task.Comment;
import com.tms.model.task.Task;
import com.tms.model.user.Admin;
import com.tms.model.user.User;
import com.tms.repository.TaskRepository;
import com.tms.service.AdminService;
import com.tms.service.CommentService;
import com.tms.service.TaskService;
import com.tms.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final String[] userTypes = {"author", "executioner"};
    private final TaskRepository taskRepository;
    private final AdminService adminService;
    private final UserService userService;
    private final CommentService commentService;

    @Override
    public Task getTask(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new EntityWithIDNotFoundException(
                Task.class.getSimpleName(), id));
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Page<List<Task>> getTasksFiltered(Long userId, String userType, int size) {
        User userToFindBy = userService.getUser(userId);
        Page<List<Task>> filteredTasks;
        Pageable pageRequest = PageRequest.of(0, size);

        if (userType.equals(userTypes[0])) {
            filteredTasks = taskRepository.findByAuthor(userToFindBy, pageRequest);
        } else if (userType.equals(userTypes[1])) {
            filteredTasks = taskRepository.findByExecutioner(userToFindBy, pageRequest);
        } else {
            throw new IllegalArgumentException("There is no valid userType in this request!");
        }
        return filteredTasks;
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

        if (taskDTO.getInitialCommentName() != null && taskDTO.getInitialCommentText() != null) {
            taskRepository.save(atomicNewTask.get());

            CommentDTO commentDTO = new CommentDTO(taskDTO.getInitialCommentName(), taskDTO.getInitialCommentText(),
                    admin.getId(), atomicNewTask.get().getId());
            Comment initialComment = commentService.createComment(commentDTO);

            atomicNewTask.get().getComments().add(initialComment);
        }
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
        if (updateTaskDTO.getTaskStatus() != null) {
            atomicTaskToUpdate.get().setTaskStatus(updateTaskDTO.getTaskStatus().get());
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
        if (updateTaskDTO.getAdditionalCommentName() != null && updateTaskDTO.getAdditionalCommentText() != null) {
            Admin admin = adminService.getAdmin();
            CommentDTO commentDTO = new CommentDTO(
                    updateTaskDTO.getAdditionalCommentName().get(),
                    updateTaskDTO.getAdditionalCommentText().get(),
                    admin.getId(),
                    atomicTaskToUpdate.get().getId());
            Comment initialComment = commentService.createComment(commentDTO);

            atomicTaskToUpdate.get().getComments().add(initialComment);
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
