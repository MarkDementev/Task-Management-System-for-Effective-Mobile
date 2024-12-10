package com.tms.controller;

import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.model.task.Task;
import com.tms.repository.TaskRepository;
import com.tms.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tms.controller.AdminController.ONLY_ADMIN;
import static com.tms.controller.TaskController.TASK_CONTROLLER_PATH;

@RestController
@RequestMapping("{base-url}" + TASK_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID_PATH = "/{id}";
    public static final String ADMIN_UPDATE_PATH = "/by-admin-up";
    public static final String USER_UPDATE_PATH = "/by-user-up";
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    //TODO полагаю, нужно 3 метода с ГЕТ - один для получения одной задачи по айди для админа, второй для получения
    //TODO всех задач конкретным автором, третий для получения всех задач конкретным исполнителем

    @Operation(summary = "Get all tasks / Method for admin only")
    @ApiResponses(@ApiResponse(responseCode = "200", content = @Content(
            schema = @Schema(implementation = Task.class)))
    )
    @GetMapping
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<List<Task>> getTasksByAdmin() {
        return ResponseEntity.ok().body(taskService.getTasks());
    }

    @Operation(summary = "Create new task / Method for admin only")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<Task> createTask(@RequestBody @Valid TaskDTO taskDTO) {
        return ResponseEntity.created(null).body(taskService.createTask(taskDTO));
    }

    @Operation(summary = "Update task / Method for admin only")
    @ApiResponse(responseCode = "200", description = "Task updated", content = @Content(
            schema = @Schema(implementation = Task.class))
    )
    @PutMapping(ADMIN_UPDATE_PATH + ID_PATH)
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<Task> updateTaskByAdmin(@PathVariable Long id,
                                           @RequestBody @Valid UpdateTaskAdminDTO updateTaskDTO) {
        return ResponseEntity.ok().body(taskService.updateTask(id, updateTaskDTO));
    }

    @Operation(summary = "Update task / Method for user only")
    @ApiResponse(responseCode = "200", description = "Task updated", content = @Content(
            schema = @Schema(implementation = Task.class))
    )
    @PutMapping(USER_UPDATE_PATH + ID_PATH)
    @PreAuthorize("@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()")
    public ResponseEntity<Task> updateTaskByUser(@PathVariable Long id,
                                                 @RequestBody @Valid UpdateTaskDTO updateTaskDTO) {
        return ResponseEntity.ok().body(taskService.updateTask(id, updateTaskDTO));
    }

    @Operation(summary = "Delete task / Method for admin only")
    @ApiResponse(responseCode = "200", description = "Task deleted")
    @DeleteMapping(ID_PATH)
    @PreAuthorize(ONLY_ADMIN)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
