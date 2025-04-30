package ru.vsu.practice.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@RequestParam String name, @RequestParam int age) {
        return userService.addUser(name, age);
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public boolean deleteUser(@PathVariable long id) {
        return userService.deleteUser(id);
    }
}
