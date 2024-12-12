package com.tms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tms.dto.LoginDto;
import com.tms.dto.TaskDTO;
import com.tms.dto.UserDTO;
import com.tms.enumeration.TaskPriority;
import com.tms.model.user.Admin;
import com.tms.repository.AdminRepository;
import com.tms.repository.CommentRepository;
import com.tms.repository.TaskRepository;
import com.tms.repository.UserRepository;
import com.tms.security.JWTHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SecurityConfig.LOGIN_PATH;
import static com.tms.controller.TaskController.TASK_CONTROLLER_PATH;
import static com.tms.controller.UserController.USER_CONTROLLER_PATH;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    private static final String ADMIN_PASSWORD = "1q2w3e";
    private static final String DEFAULT_USER_EMAIL = "default_user@google.com";
    private static final String DEFAULT_USER_PASSWORD = "aaa765";
    private static final String DEFAULT_TASK_TITLE = "title";
    private static final String DEFAULT_TASK_DESCRIPTION = "description";
    private static final String DEFAULT_TASK_COMMENT_NAME = "initialCommentName";
    private static final String DEFAULT_TASK_COMMENT_TEXT = "initialCommentText";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final LoginDto adminLoginDto = new LoginDto(
            ADMIN_NAME,
            ADMIN_PASSWORD
    );
    private final UserDTO adminDto = new UserDTO(
            ADMIN_NAME,
            ADMIN_PASSWORD
    );
    private final UserDTO defaultUserDTO = new UserDTO(
            DEFAULT_USER_EMAIL,
            DEFAULT_USER_PASSWORD
    );
    private final UserDTO updateAdminDTO = new UserDTO(
            "!" + ADMIN_NAME,
            new StringBuilder(ADMIN_PASSWORD).reverse().toString()
    );
    private final UserDTO updateUserDTO = new UserDTO(
            "!" + defaultUserDTO.getEmail(),
            new StringBuilder(DEFAULT_USER_PASSWORD).reverse().toString()
    );
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JWTHelper jwtHelper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    public void addAdmin() {
        String passwordEncoded = passwordEncoder.encode(adminDto.getPassword());
        Admin admin = new Admin(adminDto.getEmail(), passwordEncoded);

        adminRepository.save(admin);
    }

    public void tearDown() {
        commentRepository.deleteAll();
        taskRepository.deleteAll();
        adminRepository.deleteAll();
        userRepository.deleteAll();
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));

        request.header(AUTHORIZATION, token);
        return perform(request);
    }

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

    public ResultActions login(final LoginDto loginDto) throws Exception {
        return perform(post(LOGIN_PATH).content(asJson(loginDto)).contentType(APPLICATION_JSON));
    }

    public LoginDto getAdminLoginDto() {
        return adminLoginDto;
    }

    public UserDTO getUpdateAdminDTO() {
        return updateAdminDTO;
    }

    public UserDTO getUpdateUserDTO() {
        return updateUserDTO;
    }

    public UserDTO getDefaultUserDTO() {
        return defaultUserDTO;
    }

    public ResultActions createDefaultUser() throws Exception {
        return createUser(defaultUserDTO);
    }

    public ResultActions createDefaultTask() throws Exception {
        createDefaultUser();

        TaskDTO taskDTO = new TaskDTO(
                DEFAULT_TASK_TITLE,
                DEFAULT_TASK_DESCRIPTION,
                TaskPriority.AVERAGE,
                userRepository.findByEmail(defaultUserDTO.getEmail()).get().getId(),
                DEFAULT_TASK_COMMENT_NAME,
                DEFAULT_TASK_COMMENT_TEXT
        );

        return createTask(taskDTO);
    }

    public ResultActions createUser(UserDTO userDTO) throws Exception {
        final var request = post("/tms" + USER_CONTROLLER_PATH)
                .content(asJson(userDTO))
                .contentType(APPLICATION_JSON);

        return perform(request, ADMIN_NAME);
    }

    public ResultActions createTask(TaskDTO taskDTO) throws Exception {
        final var request = post("/tms" + TASK_CONTROLLER_PATH)
                .content(asJson(taskDTO))
                .contentType(APPLICATION_JSON);

        return perform(request, ADMIN_NAME);
    }
}
