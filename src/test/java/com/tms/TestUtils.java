package com.tms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.tms.dto.LoginDto;
import com.tms.dto.UserDTO;
import com.tms.security.JWTHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static com.tms.config.SecurityConfig.ADMIN_NAME;
import static com.tms.config.SecurityConfig.LOGIN_PATH;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    public static final String ADMIN_PASSWORD = "1q2w3e";
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final LoginDto adminLoginDto = new LoginDto(
            ADMIN_NAME,
            ADMIN_PASSWORD
    );
    private final UserDTO updateAdminDTO = new UserDTO(
            "!" + ADMIN_NAME,
            "123456"
    );
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JWTHelper jwtHelper;

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
}
