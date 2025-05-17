package ru.vsu.practice.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Загружает список пользователей из файла users.json при старте и сохраняет при каждом изменении.
 * Реализует CRUD-операции и управление списком друзей.
 */
public class UserService {

    private final File storageFile;
    private final ObjectMapper mapper;
    private List<User> users;

    public UserService(String externalPath) {
        this.storageFile = new File(externalPath);
        this.mapper = new ObjectMapper();

        if (!storageFile.exists()) {
            try {
                try (InputStream is = getClass().getClassLoader().getResourceAsStream("users.json")) {
                    if (is == null) {
                        storageFile.getParentFile().mkdirs();
                        storageFile.createNewFile();
                        mapper.writeValue(storageFile, new ArrayList<User>());
                    } else {
                        storageFile.getParentFile().mkdirs();
                        Files.copy(is, storageFile.toPath());
                    }
                }
                loadUsers();
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize UserService", e);
            }
        } else {
            loadUsers();
        }
        System.out.println("UserService using storage file: " + storageFile.getAbsolutePath());
        System.out.println("File exists: " + storageFile.exists());
        System.out.println("File size: " + (storageFile.exists() ? storageFile.length() : "N/A"));
    }

    /**
     * Возвращает всех пользователей с возможностью фильтрации.
     *
     * @param filters карта параметров: firstName, lastName, age, email
     * @return список подходящих пользователей
     */
    public synchronized List<User> getAll(Map<String, String> filters) {
        return users.stream()
                .filter(applyFilters(filters))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает пользователя по UID.
     *
     * @param uid уникальный идентификатор
     * @return найденный пользователь
     * @throws NoSuchElementException если пользователь не найден
     */
    public synchronized User getById(String uid) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), uid))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("User not found: " + uid));
    }

    /**
     * Возвращает друзей пользователя с фильтрацией.
     *
     * @param uid     UID пользователя
     * @param filters карта параметров фильтрации
     * @return список друзей
     */
    public synchronized List<User> getFriends(String uid, Map<String, String> filters) {
        User user = getById(uid);
        return users.stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .filter(applyFilters(filters))
                .collect(Collectors.toList());
    }

    /**
     * Создает нового пользователя.
     *
     * @param user объект пользователя (id генерируется автоматически)
     * @return созданный пользователь
     * @throws IllegalArgumentException если данные некорректны
     */
    public synchronized User create(User user) throws IllegalArgumentException {
        validateUser(user);
        users.add(user);
        saveUsers();
        return user;
    }

    /**
     * Обновляет существующего пользователя (кроме uid и email).
     *
     * @param uid   UID пользователя
     * @param patch объект с новыми данными
     * @return обновленный пользователь
     * @throws IllegalArgumentException если данные некорректны
     */
    public synchronized User update(String uid, User patch) throws IllegalArgumentException {
        User existingUser = getById(uid);

        if (patch.getFirstName() != null) {
            existingUser.setFirstName(patch.getFirstName());
        }
        if (patch.getLastName() != null) {
            existingUser.setLastName(patch.getLastName());
        }
        if (patch.getAge() != 0) {
            existingUser.setAge(patch.getAge());
        }

        if (patch.getDescription() != null) {
            existingUser.setDescription(patch.getDescription());
        }
        if (patch.getFriends() != null) {
            existingUser.setFriends(patch.getFriends());
        }

        saveUsers();

        return existingUser;
    }

    /**
     * Удаляет пользователя и исключает его из списков друзей других.
     *
     * @param uid UID пользователя для удаления
     */
    public synchronized void delete(String uid) {
        users.removeIf(u -> Objects.equals(u.getId(), uid));
        users.forEach(u -> u.getFriends().remove(uid));
        saveUsers();
    }

    /**
     * Добавляет друга к пользователю.
     *
     * @param uid       UID пользователя
     * @param friendUid UID друга
     * @throws IllegalArgumentException если UID невалидный
     */
    public synchronized void addFriend(String uid, String friendUid) throws IllegalArgumentException {
        User user = getById(uid);
        getById(friendUid); // проверка существования
        user.addFriend(friendUid);
        saveUsers();
    }

    /**
     * Удаляет друга из списка пользователя.
     *
     * @param uid       UID пользователя
     * @param friendUid UID друга
     * @throws IllegalArgumentException если UID невалидный
     */
    public synchronized void removeFriend(String uid, String friendUid) throws IllegalArgumentException {
        User user = getById(uid);
        user.removeFriend(friendUid);
        saveUsers();
    }

    // Вспомогательные методы

    private void loadUsers() {
        try {
            if (storageFile.exists()) {
                users = mapper.readValue(storageFile, new TypeReference<List<User>>() {});
            } else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from file", e);
        }
    }

    private void saveUsers() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(storageFile, users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to file", e);
        }
    }

    private Predicate<User> applyFilters(Map<String, String> filters) {
        return u -> filters.entrySet().stream().allMatch(entry -> {
            String key = entry.getKey();
            String value = entry.getValue().toLowerCase();
            return switch (key) {
                case "firstName" -> u.getFirstName().toLowerCase().contains(value);
                case "lastName" -> u.getLastName().toLowerCase().contains(value);
                case "email" -> u.getEmail().toLowerCase().contains(value);
                case "age" -> Integer.toString(u.getAge()).equals(value);
                default -> true;
            };
        });
    }

    private void validateUser(User user) throws IllegalArgumentException {
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setAge(user.getAge());
        user.setEmail(user.getEmail());
        user.setDescription(user.getDescription());
        user.setFriends(user.getFriends());
    }
}
