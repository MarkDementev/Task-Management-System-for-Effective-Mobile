package com.tms.security;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tms.dto.LoginDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

import java.util.Map;
import java.util.stream.Collectors;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final JWTHelper jwtHelper;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, RequestMatcher loginRequest,
                                   JWTHelper jwtHelper) {
        super(authenticationManager);
        super.setRequiresAuthenticationRequestMatcher(loginRequest);

        this.jwtHelper = jwtHelper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        final LoginDto loginData = getLoginData(request);
        final var authRequest = new UsernamePasswordAuthenticationToken(
                loginData.getEmail(),
                loginData.getPassword()
        );

        setDetails(request, authRequest);
        return getAuthenticationManager().authenticate(authRequest);
    }

    private LoginDto getLoginData(HttpServletRequest request) throws AuthenticationException {
        try {
            final String json = request.getReader()
                    .lines()
                    .collect(Collectors.joining());

            return MAPPER.readValue(json, LoginDto.class);
        } catch (IOException e) {
            throw new BadCredentialsException("Can't extract login data from request!");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException {
        final UserDetails user = (UserDetails) authResult.getPrincipal();
        final String token = jwtHelper.expiring(Map.of(SPRING_SECURITY_FORM_USERNAME_KEY, user.getUsername()));

        response.getWriter().println(token);
    }
}
