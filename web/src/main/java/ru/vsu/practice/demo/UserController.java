package ru.vsu.practice.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController() {
        // Путь к файлу можно вынести в конфиг, если нужно
        this.userService = new UserService("users.json");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam Map<String, String> filters) {
        return ResponseEntity.ok(userService.getAll(filters));
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getUserById(@PathVariable String uid) {
        return ResponseEntity.ok(userService.getById(uid));
    }

    @GetMapping("/user/{uid}/friends")
    public ResponseEntity<List<User>> getUserFriends(
            @PathVariable String uid,
            @RequestParam Map<String, String> filters
    ) {
        return ResponseEntity.ok(userService.getFriends(uid, filters));
    }

    @DeleteMapping("/user/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable String uid) {
        userService.delete(uid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/user/{uid}")
    public ResponseEntity<User> updateUser(@PathVariable String uid, @RequestBody User patch) {
        User updated = userService.update(uid, patch);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/user/{uid}/friends/add")
    public ResponseEntity<Void> addFriend(@PathVariable String uid, @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        userService.addFriend(uid, friendUid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{uid}/friends/rm")
    public ResponseEntity<Void> removeFriend(@PathVariable String uid, @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        userService.removeFriend(uid, friendUid);
        return ResponseEntity.noContent().build();
    }
}
