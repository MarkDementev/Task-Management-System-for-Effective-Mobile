package com.tms.config;

import com.tms.security.JWTAuthenticationFilter;
import com.tms.security.JWTAuthorizationFilter;
import com.tms.security.JWTHelper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    public static final String LOGIN_PATH = "/login";
    public static final String ADMIN_NAME = "admin_mail@mail.ru";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    private final String baseUrl;
    private final UserDetailsService userDetailsService;
    private final JWTHelper jwtHelper;
    private final RequestMatcher loginRequest;
    private final RequestMatcher publicUrl;

    public SecurityConfig(@Value("${base-url}") final String baseUrl,
                          UserDetailsService userDetailsService,
                          JWTHelper jwtHelper) {
        this.baseUrl = baseUrl;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.loginRequest = new AntPathRequestMatcher(LOGIN_PATH, POST.toString());
        this.publicUrl = new OrRequestMatcher(loginRequest);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationProvider(authenticationProvider())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(loginRequest).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(
                        new JWTAuthenticationFilter(
                                authenticationManager(http.getSharedObject(AuthenticationConfiguration.class)),
                                loginRequest,
                                jwtHelper
                        )
                )
                .addFilterBefore(
                        new JWTAuthorizationFilter(publicUrl, jwtHelper),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
