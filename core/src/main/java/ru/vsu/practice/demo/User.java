package ru.vsu.practice.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Класс, представляющий пользователя системы.
 * Включает базовые поля, валидацию данных и операции управления друзьями.
 */
public class User {

    /** Паттерн для валидации email. */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /** Паттерн для валидации имени и фамилии (только латинские буквы). */
    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z]+$");

    /** Паттерн для проверки UUID. */
    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-"
                    + "[0-9a-fA-F]{4}-"
                    + "[1-5][0-9a-fA-F]{3}-"
                    + "[89abAB][0-9a-fA-F]{3}-"
                    + "[0-9a-fA-F]{12}$");

    /** Уникальный идентификатор пользователя. */
    private String id;

    /** Имя пользователя. */
    private String firstName;

    /** Фамилия пользователя. */
    private String lastName;

    /** Возраст пользователя. */
    private int age;

    /** Электронная почта. */
    private String email;

    /** Описание пользователя. */
    private String description;

    /** Список UID друзей. */
    private List<String> friends = new ArrayList<>();

    /**
     * Конструктор по умолчанию, необходимый
     * для корректной десериализации (Jackson).
     */
    public User() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Создает нового пользователя.
     *
     * @param firstName имя
     * @param lastName фамилия
     * @param age возраст
     * @param email email
     * @param description описание
     * @param friends список друзей
     * @throws IllegalArgumentException если переданные данные некорректны
     */
    public User(final String firstName, final String lastName, final int age,
                final String email, final String description,
                final List<String> friends) {
        this.id = UUID.randomUUID().toString();
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setEmail(email);
        setDescription(description);
        setFriends(friends);
    }

    /**
     * Генерирует и присваивает ID, если он отсутствует.
     *
     * @param user пользователь
     * @return пользователь с ID
     */
    public User create(User user) {
        if (user.id == null) {
            user.id = UUID.randomUUID().toString();
        }
        return user;
    }

    /**
     * Возвращает уникальный идентификатор пользователя.
     *
     * @return ID пользователя
     */
    public String getId() {
        return this.id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Возвращает фамилию пользователя.
     *
     * @return фамилия пользователя
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Возвращает возраст пользователя.
     *
     * @return возраст пользователя
     */
    public int getAge() {
        return this.age;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Возвращает описание пользователя.
     *
     * @return описание пользователя
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Возвращает копию списка UID друзей пользователя.
     *
     * @return список UID друзей
     */
    public List<String> getFriends() {
        return new ArrayList<>(friends);
    }

    /**
     * Устанавливает ID пользователя.
     *
     * @param uid уникальный идентификатор
     */
    public void setId(String uid) {
        this.id = uid;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя
     * @throws IllegalArgumentException если имя null или содержит недопустимые символы
     */
    public void setFirstName(String firstName) {
        if (firstName == null || !NAME_PATTERN.matcher(firstName).matches()) {
            throw new IllegalArgumentException(
                    "First name must contain only letters.");
        }
        this.firstName = firstName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия
     * @throws IllegalArgumentException если фамилия null или содержит недопустимые символы
     */
    public void setLastName(String lastName) {
        if (lastName == null || !NAME_PATTERN.matcher(lastName).matches()) {
            throw new IllegalArgumentException(
                    "Last name must contain only letters.");
        }
        this.lastName = lastName;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param age возраст
     * @throws IllegalArgumentException если возраст меньше 12
     */
    public void setAge(int age) {
        if (age < 12) {
            throw new IllegalArgumentException(
                    "Age must be at least 12. See site age policy.");
        }
        this.age = age;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email-адрес
     * @throws IllegalArgumentException если email null или в некорректном формате
     */
    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    /**
     * Устанавливает описание пользователя.
     *
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает список друзей.
     *
     * @param friends список UID друзей
     * @throws IllegalArgumentException если UID недопустим
     */
    public void setFriends(List<String> friends) {
        if (friends == null) {
            this.friends = new ArrayList<>();
            return;
        }

        for (String uid : friends) {
            if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
                throw new IllegalArgumentException(
                        "Friend UID must be a valid UUID: " + uid);
            }
        }

        this.friends = new ArrayList<>(friends);
    }

    /**
     * Добавляет UID друга.
     *
     * @param uid UID друга
     * @throws IllegalArgumentException если UID недопустим
     */
    public void addFriend(String uid) {
        if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
            throw new IllegalArgumentException(
                    "Friend UID must be a valid UUID: " + uid);
        }
        if (!friends.contains(uid)) {
            friends.add(uid);
        }
    }

    /**
     * Удаляет UID друга.
     *
     * @param uid UID друга
     * @throws IllegalArgumentException если UID недопустим
     */
    public void removeFriend(String uid) {
        if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
            throw new IllegalArgumentException(
                    "Friend UID must be a valid UUID: " + uid);
        }
        friends.remove(uid);
    }
}
