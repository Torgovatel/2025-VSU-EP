package ru.vsu.practice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

/**
 * Главный класс запуска Spring Boot приложения.
 */
@SpringBootApplication
public final class AggregatorApplication {

    private AggregatorApplication() {
        // utility class constructor to satisfy Checkstyle
    }

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(final String[] args) {
        SpringApplication app = new SpringApplication(
                AggregatorApplication.class);
        app.setDefaultProperties(Map.of("server.port", "5050"));
        app.run(args);
    }
}
