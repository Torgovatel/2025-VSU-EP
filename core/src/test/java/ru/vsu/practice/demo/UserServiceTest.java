package ru.vsu.practice.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private File jsonFile;

    private String user1Id;
    private String user2Id;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) throws IOException {
        jsonFile = tempDir.resolve("users.json").toFile();
        ObjectMapper mapper = new ObjectMapper();

        User user1 = new User("John", "Doe", 30, "john.doe@example.com", "desc", new ArrayList<>());
        User user2 = new User("Jane", "Smith", 25, "jane.smith@example.com", "desc", new ArrayList<>());

        user1Id = user1.getId();
        user2Id = user2.getId();

        List<User> initialUsers = List.of(user1, user2);
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, initialUsers);

        userService = new UserService(jsonFile.getAbsolutePath());
    }

    /**
     * Проверяет, что метод getAll возвращает всех пользователей.
     */
    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAll(Collections.emptyMap());
        assertEquals(2, users.size());
    }

    @Test
    public void testGetAllUsersWithSpy() {
        UserService spyService = spy(userService);

        User user1 = new User("Mock", "User", 20, "mock@example.com", "desc", List.of());
        doReturn(List.of(user1)).when(spyService).getAll(anyMap());

        List<User> users = spyService.getAll(Collections.emptyMap());

        assertEquals(1, users.size());
        assertEquals("Mock", users.get(0).getFirstName());

        verify(spyService).getAll(Collections.emptyMap());
    }

    /**
     * Проверяет, что метод getById возвращает правильного пользователя по ID.
     */
    @Test
    public void testGetUserById() {
        User user = userService.getById(user1Id);
        assertEquals("John", user.getFirstName());
    }

    /**
     * Проверяет, что метод create добавляет нового пользователя.
     */
    @Test
    public void testCreateUser() {
        User newUser = new User("Alice", "Wonder", 28, "alice@example.com", "desc", new ArrayList<>());
        userService.create(newUser);

        assertEquals(3, userService.getAll(Collections.emptyMap()).size());

        Map<String, String> filters = new HashMap<>();
        filters.put("email", "alice@example.com");
        List<User> filteredUsers = userService.getAll(filters);

        assertEquals(1, filteredUsers.size());

        User foundUser = filteredUsers.get(0);

        assertEquals("Alice", foundUser.getFirstName());
        assertEquals("Wonder", foundUser.getLastName());
        assertEquals(28, foundUser.getAge());
        assertEquals("alice@example.com", foundUser.getEmail());
        assertEquals("desc", foundUser.getDescription());
        assertTrue(foundUser.getFriends().isEmpty());
    }

    @Test
    public void testCreateInvalidUser() {
        boolean ageExceptionThrown = false;
        try {
            User finalInvalidUser1 = new User("Timmy", "Young", 10, "timmy@example.com", "desc", new ArrayList<>());
            userService.create(finalInvalidUser1);
        } catch (IllegalArgumentException e) {
            ageExceptionThrown = true;
        }
        assertTrue(ageExceptionThrown, "Expected IllegalArgumentException for age < 12");

        boolean emailExceptionThrown = false;
        try {
            User finalInvalidUser2 = new User("Bob", "Email", 20, "invalid-email", "desc", new ArrayList<>());
            userService.create(finalInvalidUser2);
        } catch (IllegalArgumentException e) {
            emailExceptionThrown = true;
        }
        assertTrue(emailExceptionThrown, "Expected IllegalArgumentException for invalid email");
    }

    /**
     * Проверяет, что метод update корректно обновляет данные пользователя.
     */
    @Test
    public void testUpdateUser() {
        User patch = new User("Johnny", "Doe", 31, "john.doe@example.com", "updated", new ArrayList<>());
        User updated = userService.update(user1Id, patch);

        assertEquals("Johnny", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        assertEquals(31, updated.getAge());
        assertEquals("updated", updated.getDescription());
        assertEquals("john.doe@example.com", updated.getEmail());
        assertTrue(updated.getFriends().isEmpty());
    }

    /**
     * Проверяет, что метод delete удаляет пользователя по ID и его больше нельзя получить.
     */
    @Test
    public void testDeleteUser() {
        userService.delete(user1Id);
        assertEquals(1, userService.getAll(Collections.emptyMap()).size());
        assertThrows(NoSuchElementException.class, () -> userService.getById(user1Id));
    }

    /**
     * Проверяет, что методы addFriend и removeFriend корректно добавляют и удаляют друга.
     */
    @Test
    public void testAddAndRemoveFriend() {
        userService.addFriend(user1Id, user2Id);
        List<String> friendsIds = userService.getById(user1Id).getFriends();
        assertTrue(friendsIds.contains(user2Id));

        User friend = userService.getById(user2Id);
        assertNotNull(friend);
        assertEquals("Jane", friend.getFirstName());
        assertEquals("Smith", friend.getLastName());
        assertEquals(25, friend.getAge());
        assertEquals("jane.smith@example.com", friend.getEmail());

        userService.removeFriend(user1Id, user2Id);
        friendsIds = userService.getById(user1Id).getFriends();
        assertFalse(friendsIds.contains(user2Id));
    }

    /**
     * Проверяет, что метод getFriends возвращает список друзей пользователя.
     */
    @Test
    public void testGetFriends() {
        userService.addFriend(user1Id, user2Id);
        List<User> friends = userService.getFriends(user1Id, Collections.emptyMap());
        assertEquals(1, friends.size());

        User friend = friends.get(0);
        assertEquals(user2Id, friend.getId());
        assertEquals("Jane", friend.getFirstName());
        assertEquals("Smith", friend.getLastName());
        assertEquals(25, friend.getAge());
        assertEquals("jane.smith@example.com", friend.getEmail());
    }

    private List<User> readUsersFromJsonFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(file, User[].class));
    }

    /**
     * Тест проверяет загрузку пользователей из resources/users.json, их корректное чтение,
     * удаление одного пользователя, сохранение и восстановление.
     *
     * @param tempDir временная директория для тестовых файлов
     */
    @Test
    public void testLoadDeleteAddUserFromResource(@TempDir Path tempDir) throws IOException {
        // Копируем users.json из ресурсов во временную директорию
        try (InputStream resourceStream = getClass().getResourceAsStream("/users.json")) {
            assertNotNull(resourceStream, "Resource users.json not found");

            File testFile = tempDir.resolve("users.json").toFile();
            Files.copy(resourceStream, testFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            UserService userService = new UserService(testFile.getAbsolutePath());

            // 1) Проверяем, что пользователей ровно 10
            List<User> users = userService.getAll(Collections.emptyMap());
            assertEquals(10, users.size(), "Должно быть ровно 10 пользователей");

            // Проверяем корректность email всех пользователей из файла
            Set<String> expectedEmails = Set.of(
                    "alice.smith@example.com",
                    "bob.brown@example.com",
                    "charlie.davis@example.com",
                    "diana.evans@example.com",
                    "ethan.foster@example.com",
                    "fiona.garcia@example.com",
                    "george.hill@example.com",
                    "hannah.irwin@example.com",
                    "ian.jones@example.com",
                    "julia.king@example.com"
            );

            Set<String> actualEmails = users.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toSet());

            assertEquals(expectedEmails, actualEmails, "Email пользователей не совпадают с ожидаемыми");

            // 2) Удаляем первого пользователя из списка
            User userToDelete = users.get(0);
            userService.delete(userToDelete.getId());

            // 3) Загружаем пользователей снова, должно быть 9
            UserService userServiceReloaded = new UserService(testFile.getAbsolutePath());
            List<User> usersAfterDelete = userServiceReloaded.getAll(Collections.emptyMap());
            assertEquals(9, usersAfterDelete.size(), "Должно остаться 9 пользователей после удаления");

            // Проверяем что email удаленного пользователя отсутствует
            Set<String> emailsAfterDelete = usersAfterDelete.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toSet());
            assertFalse(emailsAfterDelete.contains(userToDelete.getEmail()), "Email удаленного пользователя присутствует");

            // 4) Добавляем удаленного пользователя обратно
            userServiceReloaded.create(userToDelete);

            // 5) Загружаем снова, проверяем что 10 и email совпадают с оригиналом
            UserService userServiceFinal = new UserService(testFile.getAbsolutePath());
            List<User> usersFinal = userServiceFinal.getAll(Collections.emptyMap());
            assertEquals(10, usersFinal.size(), "Должно быть 10 пользователей после восстановления");

            Set<String> emailsFinal = usersFinal.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toSet());

            assertEquals(expectedEmails, emailsFinal, "Email пользователей после восстановления не совпадают с оригиналом");
        }
    }
}
