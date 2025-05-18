package ru.vsu.practice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * Главный класс запуска Spring Boot приложения.
 */
// CHECKSTYLE:OFF: HideUtilityClassConstructor

@SpringBootApplication
public class AggregatorApplication {
    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(
                AggregatorApplication.class);
        app.setDefaultProperties(Map.of("server.port", "5050"));
        app.run(args);
    }
}