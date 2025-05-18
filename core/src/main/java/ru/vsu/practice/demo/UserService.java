package ru.vsu.practice.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями.
 * Загружает список пользователей из файла users.json при старте
 * и сохраняет при каждом изменении.
 * Реализует CRUD-операции и управление списком друзей.
 */
public class UserService {

    /** Файл для хранения данных пользователей в формате JSON. */
    private final File storageFile;

    /** Объект для сериализации и десериализации пользователей. */
    private final ObjectMapper mapper;

    /** Список всех пользователей, загруженных из хранилища. */
    private List<User> users;

    /**
     * Конструктор сервиса пользователей. Загружает данные из файла
     * или создает новый файл.
     *
     * @param externalPath путь к файлу хранения users.json
     */
    public UserService(final String externalPath) {
        this.storageFile = new File(externalPath);
        this.mapper = new ObjectMapper();

        if (!storageFile.exists()) {
            try {
                try (InputStream is = getClass().getClassLoader()
                        .getResourceAsStream("users.json")) {
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
    }

    /**
     * Получает список всех пользователей с применением фильтров.
     *
     * @param filters карта фильтров: firstName, lastName, email, age
     * @return отфильтрованный список пользователей
     */
    public synchronized List<User> getAll(final Map<String, String> filters) {
        return users.stream()
                .filter(applyFilters(filters))
                .collect(Collectors.toList());
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param uid идентификатор пользователя
     * @return пользователь
     * @throws NoSuchElementException если пользователь не найден
     */
    public synchronized User getById(final String uid) {
        return users.stream()
                .filter(u -> Objects.equals(u.getId(), uid))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + uid));
    }

    /**
     * Получает список друзей пользователя с возможностью фильтрации.
     *
     * @param uid     идентификатор пользователя
     * @param filters карта фильтров
     * @return список друзей
     */
    public synchronized List<User> getFriends(
            final String uid,
            final Map<String, String> filters
    ) {
        User user = getById(uid);
        return users.stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .filter(applyFilters(filters))
                .collect(Collectors.toList());
    }

    /**
     * Создает нового пользователя.
     *
     * @param user объект пользователя
     * @return созданный пользователь
     * @throws IllegalArgumentException если данные некорректны
     */
    public synchronized User create(final User user)
            throws IllegalArgumentException {
        validateUser(user);
        users.add(user);
        saveUsers();
        return user;
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param uid   идентификатор пользователя
     * @param patch объект с обновленными полями
     * @return обновленный пользователь
     * @throws IllegalArgumentException если данные некорректны
     */
    public synchronized User update(final String uid, final User patch)
            throws IllegalArgumentException {
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
     * Удаляет пользователя по идентификатору и удаляет его из списков друзей.
     *
     * @param uid идентификатор пользователя
     */
    public synchronized void delete(final String uid) {
        users.removeIf(u -> Objects.equals(u.getId(), uid));
        users.forEach(u -> u.getFriends().remove(uid));
        saveUsers();
    }

    /**
     * Добавляет друга пользователю.
     *
     * @param uid       идентификатор пользователя
     * @param friendUid идентификатор друга
     * @throws IllegalArgumentException если пользователь или друг не найдены
     */
    public synchronized void addFriend(final String uid, final String friendUid)
            throws IllegalArgumentException {
        User user = getById(uid);
        getById(friendUid); // проверка существования
        user.addFriend(friendUid);
        saveUsers();
    }

    /**
     * Удаляет друга из списка пользователя.
     *
     * @param uid       идентификатор пользователя
     * @param friendUid идентификатор друга
     * @throws IllegalArgumentException если пользователь не найден
     */
    public synchronized void removeFriend(final String uid,
                                          final String friendUid)
            throws IllegalArgumentException {
        User user = getById(uid);
        user.removeFriend(friendUid);
        saveUsers();
    }

    /**
     * Загружает пользователей из JSON-файла.
     */
    private void loadUsers() {
        try {
            if (storageFile.exists()) {
                users = mapper.readValue(storageFile,
                        new TypeReference<List<User>>() {});
            } else {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from file", e);
        }
    }

    /**
     * Сохраняет текущий список пользователей в файл.
     */
    private void saveUsers() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(storageFile, users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to file", e);
        }
    }

    /**
     * Возвращает предикат фильтрации пользователей
     * на основе переданных параметров.
     *
     * @param filters фильтры для firstName, lastName, email, age
     * @return предикат фильтрации
     */
    private Predicate<User> applyFilters(final Map<String, String> filters) {
        return u -> filters.entrySet().stream().allMatch(entry -> {
            String key = entry.getKey();
            String value = entry.getValue().toLowerCase();
            return switch (key) {
                case "firstName" ->
                        u.getFirstName().toLowerCase().contains(value);
                case "lastName" ->
                        u.getLastName().toLowerCase().contains(value);
                case "email" -> u.getEmail().toLowerCase().contains(value);
                case "age" -> Integer.toString(u.getAge()).equals(value);
                default -> true;
            };
        });
    }

    /**
     * Валидирует и нормализует данные пользователя.
     *
     * @param user объект пользователя
     * @throws IllegalArgumentException если данные некорректны
     */
    private void validateUser(final User user) throws IllegalArgumentException {
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setAge(user.getAge());
        user.setEmail(user.getEmail());
        user.setDescription(user.getDescription());
        user.setFriends(user.getFriends());
    }
}
