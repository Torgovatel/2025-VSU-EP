package ru.vsu.practice.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController() {
        this.userService = new UserService("users.json");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam Map<String, String> filters) {
        log.info("GET /users with filters: {}", filters);
        try {
            List<User> users = userService.getAll(filters);
            log.info("Returned {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error in getAllUsers: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getUserById(@PathVariable("uid") String uid) {
        log.info("GET /user/{}", uid);
        try {
            User user = userService.getById(uid);
            log.info("Found user: {}", user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error in getUserById for uid {}: ", uid, e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{uid}/friends")
    public ResponseEntity<List<User>> getUserFriends(
            @PathVariable("uid") String uid,
            @RequestParam Map<String, String> filters) {
        log.info("GET /user/{}/friends with filters: {}", uid, filters);
        try {
            List<User> friends = userService.getFriends(uid, filters);
            log.info("Returned {} friends for user {}", friends.size(), uid);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            log.error("Error in getUserFriends for uid {}: ", uid, e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/user/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable("uid") String uid) {
        log.info("DELETE /user/{}", uid);
        try {
            userService.delete(uid);
            log.info("Deleted user with uid {}", uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting user {}: ", uid, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("POST /user with user: {}", user);
        try {
            User created = userService.create(user);
            log.info("Created user with uid {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error creating user: ", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/user/{uid}")
    public ResponseEntity<User> updateUser(@PathVariable("uid") String uid, @RequestBody User patch) {
        log.info("PUT /user/{} with patch: {}", uid, patch);
        try {
            User updated = userService.update(uid, patch);
            log.info("Updated user with uid {}", uid);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating user {}: ", uid, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/user/{uid}/friends/add")
    public ResponseEntity<Void> addFriend(@PathVariable("uid") String uid, @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        log.info("PATCH /user/{}/friends/add with friendUid: {}", uid, friendUid);
        try {
            userService.addFriend(uid, friendUid);
            log.info("Added friend {} to user {}", friendUid, uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error adding friend {} to user {}: ", friendUid, uid, e);
            return ResponseEntity.status(500).build();
        }
    }

    @PatchMapping("/user/{uid}/friends/rm")
    public ResponseEntity<Void> removeFriend(@PathVariable("uid") String uid, @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        log.info("PATCH /user/{}/friends/rm with friendUid: {}", uid, friendUid);
        try {
            userService.removeFriend(uid, friendUid);
            log.info("Removed friend {} from user {}", friendUid, uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error removing friend {} from user {}: ", friendUid, uid, e);
            return ResponseEntity.status(500).build();
        }
    }
}