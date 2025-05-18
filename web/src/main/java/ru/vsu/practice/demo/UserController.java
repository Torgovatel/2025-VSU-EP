package ru.vsu.practice.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Контроллер для управления пользователями и их друзьями.
 */
@RestController
@RequestMapping("/api/v1")
public final class UserController {

    /**
     * Инстантс логгера.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
            UserController.class);

    /**
     * Инстантс сервисного класса для вазимодействия с пользователем.
     */
    private final UserService userService;

    /**
     * Конструктор по умолчанию, использующий файл users.json.
     */
    public UserController() {
        this.userService = new UserService("users.json");
    }

    /**
     * Конструктор с внедрением зависимости UserService.
     *
     * @param userServiceParam сервис пользователей
     */
    public UserController(final UserService userServiceParam) {
        this.userService = userServiceParam;
    }

    /**
     * Получить всех пользователей с возможной фильтрацией.
     *
     * @param filters параметры фильтрации
     * @return список пользователей
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(
            final @RequestParam Map<String, String> filters) {
        LOG.info("GET /users with filters: {}", filters);
        try {
            List<User> users = userService.getAll(filters);
            LOG.info("Returned {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOG.error("Error in getAllUsers: ", e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить пользователя по ID.
     *
     * @param uid идентификатор пользователя
     * @return пользователь
     */
    @GetMapping("/user/{uid}")
    public ResponseEntity<User> getUserById(
            final @PathVariable("uid") String uid) {
        LOG.info("GET /user/{}", uid);
        try {
            User user = userService.getById(uid);
            LOG.info("Found user: {}", user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            LOG.error("Error in getUserById for uid {}: ", uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить список друзей пользователя.
     *
     * @param uid     идентификатор пользователя
     * @param filters фильтры
     * @return список друзей
     */
    @GetMapping("/user/{uid}/friends")
    public ResponseEntity<List<User>> getUserFriends(
            final @PathVariable("uid") String uid,
            final @RequestParam Map<String, String> filters) {
        LOG.info("GET /user/{}/friends with filters: {}", uid, filters);
        try {
            List<User> friends = userService.getFriends(uid, filters);
            LOG.info("Returned {} friends for user {}", friends.size(), uid);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            LOG.error("Error in getUserFriends for uid {}: ", uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удалить пользователя по ID.
     *
     * @param uid идентификатор пользователя
     * @return статус удаления
     */
    @DeleteMapping("/user/{uid}")
    public ResponseEntity<Void> deleteUser(
            final @PathVariable("uid") String uid) {
        LOG.info("DELETE /user/{}", uid);
        try {
            userService.delete(uid);
            LOG.info("Deleted user with uid {}", uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOG.error("Error deleting user {}: ", uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Создать нового пользователя.
     *
     * @param user данные пользователя
     * @return созданный пользователь
     */
    @PostMapping("/user")
    public ResponseEntity<User> createUser(final @RequestBody User user) {
        LOG.info("POST /user with user: {}", user);
        try {
            User created = userService.create(user);
            LOG.info("Created user with uid {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            LOG.error("Error creating user: ", e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Обновить информацию о пользователе.
     *
     * @param uid   идентификатор пользователя
     * @param patch данные для обновления
     * @return обновлённый пользователь
     */
    @PutMapping("/user/{uid}")
    public ResponseEntity<User> updateUser(
            final @PathVariable("uid") String uid,
            final @RequestBody User patch) {
        LOG.info("PUT /user/{} with patch: {}", uid, patch);
        try {
            User updated = userService.update(uid, patch);
            LOG.info("Updated user with uid {}", uid);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            LOG.error("Error updating user {}: ", uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Добавить друга пользователю.
     *
     * @param uid     идентификатор пользователя
     * @param payload JSON с ключом friendUid
     * @return статус выполнения
     */
    @PatchMapping("/user/{uid}/friends/add")
    public ResponseEntity<Void> addFriend(
            final @PathVariable("uid") String uid,
            final @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        LOG.info("PATCH /user/{}/friends/add with friendUid: {}",
                uid, friendUid);
        try {
            userService.addFriend(uid, friendUid);
            LOG.info("Added friend {} to user {}", friendUid, uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOG.error("Error adding friend {} to user {}: ",
                    friendUid, uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Удалить друга у пользователя.
     *
     * @param uid     идентификатор пользователя
     * @param payload JSON с ключом friendUid
     * @return статус выполнения
     */
    @PatchMapping("/user/{uid}/friends/rm")
    public ResponseEntity<Void> removeFriend(
            final @PathVariable("uid") String uid,
            final @RequestBody Map<String, String> payload) {
        String friendUid = payload.get("friendUid");
        LOG.info("PATCH /user/{}/friends/rm with friendUid: {}",
                uid, friendUid);
        try {
            userService.removeFriend(uid, friendUid);
            LOG.info("Removed friend {} from user {}", friendUid, uid);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            LOG.error("Error removing friend {} from user {}: ",
                    friendUid, uid, e);
            return ResponseEntity.status(
                    HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
