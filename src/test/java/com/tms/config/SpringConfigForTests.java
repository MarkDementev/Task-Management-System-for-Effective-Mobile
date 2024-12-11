package com.tms.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile(SpringConfigForTests.TEST_PROFILE)
@ComponentScan(basePackages = "com.tms")
@PropertySource(value = "classpath:/application-test.yml")
public class SpringConfigForTests {
    public static final String TEST_PROFILE = "test";
}