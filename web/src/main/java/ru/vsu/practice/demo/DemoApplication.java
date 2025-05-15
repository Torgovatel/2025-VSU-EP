package ru.vsu.practice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Задача: Необходимо создать приложение на Java, которое будет управлять пользователями.
 * Приложение должно позволять:
 * 1. Добавлять нового пользователя.
 * 2. Получать список всех пользователей.
 * 3. Удалять пользователя по ID.
 * Реализовать все функции с использованием Spring Boot.
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
