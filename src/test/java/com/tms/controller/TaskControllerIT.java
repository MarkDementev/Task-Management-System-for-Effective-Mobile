package com.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tms.TestUtils;
import com.tms.config.SpringConfigForTests;

import com.tms.model.task.Task;
import com.tms.model.user.User;
import com.tms.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static com.tms.TestUtils.asJson;
import static com.tms.TestUtils.fromJson;
import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SecurityConfig.LOGIN_PATH;
import static com.tms.config.SpringConfigForTests.TEST_PROFILE;

import static com.tms.controller.TaskController.TASK_CONTROLLER_PATH;
import static com.tms.controller.UserController.ID_PATH;
import static com.tms.controller.UserController.USER_CONTROLLER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                        get("/tms" + TASK_CONTROLLER_PATH + ID_PATH, expectedTask.getId()),
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
                        get("/tms" + TASK_CONTROLLER_PATH),
                        ADMIN_NAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Task> allTasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(allTasks).hasSize(2);
    }

    @Test
    public void getTasksFiltered() throws Exception {
        testUtils.createDefaultUser();

        var response = testUtils.perform(
                        get("/tms" + USER_CONTROLLER_PATH),
                        ADMIN_NAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<User> allUsers = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(allUsers).hasSize(2);
    }

//    @Test
//    public void createTaskIT() throws Exception {
//
//    }
//
//    @Test
//    public void updateTaskByAdminIT() throws Exception {
//
//    }
//
//    @Test
//    public void updateTaskByUserIT() throws Exception {
//
//    }

    @Test
    public void deleteTaskIT() throws Exception {
        testUtils.createDefaultTask();

        Long createdTaskId = taskRepository.findAll().get(0).getId();

        testUtils.perform(
                        delete("/tms" + TASK_CONTROLLER_PATH+ ID_PATH, createdTaskId),
                        ADMIN_NAME)
                .andExpect(status().isOk());
        assertEquals(0, taskRepository.findAll().size());
    }
}
