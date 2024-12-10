package com.tms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

import static com.tms.config.SecurityConfig.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer";
    private final RequestMatcher publicUrl;
    private final JWTHelper jwtHelper;

    public JWTAuthorizationFilter(RequestMatcher publicUrl, JWTHelper jwtHelper) {
        this.publicUrl = publicUrl;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicUrl.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .map(header -> header.replaceFirst("^" + BEARER, ""))
                .map(String::trim)
                .map(jwtHelper::verify)
                .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
                .map(Object::toString)
                .map(this::buildAuthToken)
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(String username) {
        List<GrantedAuthority> authorities;

        if (username.equals(ADMIN_NAME)) {
            authorities = List.of(new SimpleGrantedAuthority(ROLE_ADMIN), new SimpleGrantedAuthority(ROLE_USER));
        } else {
            authorities = List.of(new SimpleGrantedAuthority(ROLE_USER));
        }

        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
        );
    }
}
