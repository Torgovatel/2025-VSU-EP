package ru.vsu.practice.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();
    private long idCounter = 1;

    public User addUser(String name, int age) {
        User user = new User(idCounter++, name, age);
        users.add(user);
        return user;
    }

    public List<User> getUsers() {
        return users;
    }

    public boolean deleteUser(long id) {
        return users.removeIf(user -> user.getId() == id);
    }
}
