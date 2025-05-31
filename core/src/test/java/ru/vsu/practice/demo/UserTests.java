package ru.vsu.practice.demo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTests {
    @Test
    public void testCreateUser() {
        User newUser = new User("Alice", "Wonder", 28, "alice@example.com", "desc", new ArrayList<>());
        newUser.setId(null);
        User tmpUser = newUser.create(newUser);
        assertTrue(tmpUser.getId().length()>0);
    }
}
