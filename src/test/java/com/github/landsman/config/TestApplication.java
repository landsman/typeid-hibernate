package com.github.landsman.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Test application class for Spring Boot tests.
 * This class serves as the entry point for component scanning.
 */
@SpringBootApplication
@Import(TestConfig.class)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
