package ru.vsu.practice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class AggregatorApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AggregatorApplication.class);
        app.setDefaultProperties(Map.of("server.port", "5050"));
        app.run(args);
    }
}