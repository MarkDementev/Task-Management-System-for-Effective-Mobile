package com.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;

import com.tms.TestUtils;
import com.tms.config.SpringConfigForTests;
import com.tms.dto.TaskDTO;
import com.tms.dto.update.UpdateTaskAdminDTO;
import com.tms.dto.update.UpdateTaskDTO;
import com.tms.enumeration.TaskPriority;
import com.tms.enumeration.TaskStatus;
import com.tms.model.task.Comment;
import com.tms.model.task.Task;
import com.tms.model.user.User;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.tms.TestUtils.*;
import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SpringConfigForTests.TEST_PROFILE;
import static com.tms.controller.TaskController.*;
import static com.tms.controller.UserController.ID_PATH;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringConfigForTests.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles(TEST_PROFILE)
@AutoConfigureMockMvc
public class TaskControllerIT {
    @Autowired
    private TestUtils testUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void beforeTest() throws Exception {
        testUtils.addAdmin();
        testUtils.login(testUtils.getAdminLoginDto());
    }

    @AfterEach
    public void clearRepositories() {
        testUtils.tearDown();
    }

    @Test
    public void getTaskIT() throws Exception {
        testUtils.createDefaultTask();

        Task expectedTask = taskRepository.findAll().get(0);
        var response = testUtils.perform(
                        get(testUtils.baseUrl + TASK_CONTROLLER_PATH + ID_PATH, expectedTask.getId()),
                        ADMIN_NAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Task taskFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getId(), taskFromResponse.getId());
        assertEquals(expectedTask.getTitle(), taskFromResponse.getTitle());
        assertEquals(expectedTask.getDescription(), taskFromResponse.getDescription());
        assertEquals(expectedTask.getTaskStatus(), taskFromResponse.getTaskStatus());
        assertEquals(expectedTask.getTaskPriority(), taskFromResponse.getTaskPriority());
        assertEquals(expectedTask.getComments().get(0).getId(), taskFromResponse.getComments().get(0).getId());
        assertEquals(expectedTask.getAuthor().getEmail(), taskFromResponse.getAuthor().getEmail());
        assertEquals(expectedTask.getExecutor().getEmail(), taskFromResponse.getExecutor().getEmail());
        assertNotNull(taskFromResponse.getCreatedAt());
        assertNotNull(taskFromResponse.getUpdatedAt());
    }

    @Test
    public void getTasksByAdminIT() throws Exception {
        testUtils.createDefaultTask();

        var response = testUtils.perform(
                        get(testUtils.baseUrl + TASK_CONTROLLER_PATH),
                        ADMIN_NAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Task> allTasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(allTasks).hasSize(1);
    }

    @Test
    public void getTasksFiltered() throws Exception {
        testUtils.createDefaultTask();

        var responseWhenFindAdminExecutor = testUtils.perform(
                        get(testUtils.baseUrl + TASK_CONTROLLER_PATH + USER_FILTER_PATH + ID_PATH,
                                userRepository.findAll().get(0).getId())
                                .param("userType", "executor").param("limit", "1"),
                        ADMIN_NAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(responseWhenFindAdminExecutor.getContentAsString().contains("\"numberOfElements\":0"));

        var responseWhenFindUserExecutor = testUtils.perform(
                        get(testUtils.baseUrl + TASK_CONTROLLER_PATH + USER_FILTER_PATH + ID_PATH,
                                userRepository.findAll().get(1).getId())
                                .param("userType", "executor").param("limit", "1"),
                        ADMIN_NAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertTrue(responseWhenFindUserExecutor.getContentAsString().contains("\"numberOfElements\":1"));
    }

    @Test
    public void createTaskIT() throws Exception {
        testUtils.createDefaultUser();

        User createdDefaultUser = userRepository.findByEmail(testUtils.getDefaultUserDTO().getEmail()).get();
        TaskDTO taskDTO = new TaskDTO(
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                TaskPriority.HIGH,
                createdDefaultUser.getId(),
                DEFAULT_TASK_COMMENT_NAME,
                DEFAULT_TASK_COMMENT_TEXT
        );
        var response = testUtils.perform(
                        post(testUtils.baseUrl + TASK_CONTROLLER_PATH)
                                .content(asJson(taskDTO))
                                .contentType(APPLICATION_JSON),
                        ADMIN_NAME
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        Task taskFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        Comment commentCreatedWithTask = commentRepository.findAll().get(0);

        assertEquals(taskDTO.getTitle(), taskFromResponse.getTitle());
        assertEquals(taskDTO.getDescription(), taskFromResponse.getDescription());
        assertEquals(TaskStatus.WAITING, taskFromResponse.getTaskStatus());
        assertEquals(taskDTO.getTaskPriority(), taskFromResponse.getTaskPriority());
        assertEquals(commentCreatedWithTask.getId(), taskFromResponse.getComments().get(0).getId());
        assertEquals(commentCreatedWithTask.getName(), taskFromResponse.getComments().get(0).getName());
        assertEquals(commentCreatedWithTask.getText(), taskFromResponse.getComments().get(0).getText());
        assertEquals(ADMIN_NAME, taskFromResponse.getAuthor().getEmail());
        assertEquals(createdDefaultUser.getEmail(), taskFromResponse.getExecutor().getEmail());
        assertNotNull(taskFromResponse.getCreatedAt());
        assertNotNull(taskFromResponse.getUpdatedAt());
    }

    @Test
    public void updateTaskByAdminIT() throws Exception {
        testUtils.createDefaultTask();

        UpdateTaskAdminDTO taskUpdateAdminDTO = new UpdateTaskAdminDTO(
                JsonNullable.of(TaskStatus.IN_PROCESS),
                JsonNullable.of(new StringBuilder(DEFAULT_TASK_DESCRIPTION).reverse().toString()),
                JsonNullable.of(TaskPriority.HIGH),
                JsonNullable.of(userRepository.findByEmail(DEFAULT_USER_EMAIL).get().getId())
        );
        Long createdTaskId = taskRepository.findAll().get(0).getId();
        var response = testUtils.perform(
                        put(testUtils.baseUrl + TASK_CONTROLLER_PATH + ADMIN_UPDATE_PATH + ID_PATH,
                                createdTaskId)
                                .content(asJson(taskUpdateAdminDTO))
                                .contentType(APPLICATION_JSON),
                        ADMIN_NAME
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Task taskFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(taskUpdateAdminDTO.getTaskStatus().get(), taskFromResponse.getTaskStatus());
        assertEquals(taskUpdateAdminDTO.getDescription().get(), taskFromResponse.getDescription());
        assertEquals(taskUpdateAdminDTO.getTaskPriority().get(), taskFromResponse.getTaskPriority());
        assertEquals(taskUpdateAdminDTO.getExecutorID().get(), taskFromResponse.getExecutor().getId());
    }

    @Test
    public void updateTaskByUserIT() throws Exception {
        testUtils.createDefaultTask();

        UpdateTaskDTO taskUpdateDTO = new UpdateTaskDTO(
                JsonNullable.of(TaskStatus.IN_PROCESS)
        );
        Long createdTaskId = taskRepository.findAll().get(0).getId();
        var response = testUtils.perform(
                        put(testUtils.baseUrl + TASK_CONTROLLER_PATH + USER_UPDATE_PATH + ID_PATH,
                                createdTaskId)
                                .content(asJson(taskUpdateDTO))
                                .contentType(APPLICATION_JSON),
                        userRepository.findByEmail(DEFAULT_USER_EMAIL).get().getEmail()
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Task taskFromResponse = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(taskUpdateDTO.getTaskStatus().get(), taskFromResponse.getTaskStatus());
    }

    @Test
    public void deleteTaskIT() throws Exception {
        testUtils.createDefaultTask();

        Long createdTaskId = taskRepository.findAll().get(0).getId();

        testUtils.perform(
                        delete(testUtils.baseUrl + TASK_CONTROLLER_PATH+ ID_PATH, createdTaskId),
                        ADMIN_NAME)
                .andExpect(status().isOk());
        assertEquals(0, taskRepository.findAll().size());
    }
}
