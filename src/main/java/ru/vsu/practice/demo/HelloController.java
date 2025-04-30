package ru.vsu.practice.demo;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Привет, дорогой друг!";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(defaultValue = "Гость") String name) {
        return "Привет, " + name + "!";
    }
}
