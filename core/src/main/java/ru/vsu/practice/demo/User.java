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
     * @param firstNameParam имя
     * @param lastNameParam фамилия
     * @param ageParam возраст
     * @param emailParam email
     * @param descriptionParam описание
     * @param friendsParam список друзей
     * @throws IllegalArgumentException если переданные данные некорректны
     */
    public User(final String firstNameParam,
                final String lastNameParam, final int ageParam,
                final String emailParam, final String descriptionParam,
                final List<String> friendsParam) {
        this.id = UUID.randomUUID().toString();
        setFirstName(firstNameParam);
        setLastName(lastNameParam);
        setAge(ageParam);
        setEmail(emailParam);
        setDescription(descriptionParam);
        setFriends(friendsParam);
    }

    /**
     * Генерирует и присваивает ID, если он отсутствует.
     *
     * @param user пользователь
     * @return пользователь с ID
     */
    public User create(final User user) {
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
    public void setId(final String uid) {
        this.id = uid;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstNameParam имя
     * @throws IllegalArgumentException если имя null
     * или содержит недопустимые символы
     */
    public void setFirstName(final String firstNameParam) {
        if (firstNameParam == null
                || !NAME_PATTERN.matcher(firstNameParam).matches()) {
            throw new IllegalArgumentException(
                    "First name must contain only letters.");
        }
        this.firstName = firstNameParam;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastNameParam фамилия
     * @throws IllegalArgumentException если фамилия null
     * или содержит недопустимые символы
     */
    public void setLastName(final String lastNameParam) {
        if (lastNameParam == null
                || !NAME_PATTERN.matcher(lastNameParam).matches()) {
            throw new IllegalArgumentException(
                    "Last name must contain only letters.");
        }
        this.lastName = lastNameParam;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param ageParam возраст
     * @throws IllegalArgumentException если возраст меньше 12
     */
    public void setAge(final int ageParam) {
        final int maxReqAge = 12;
        if (ageParam < maxReqAge) {
            throw new IllegalArgumentException(
                    "Age must be at least 12. See site age policy.");
        }
        this.age = ageParam;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param emailParam email-адрес
     * @throws IllegalArgumentException если email null
     * или в некорректном формате
     */
    public void setEmail(final String emailParam) {
        if (emailParam == null
                || !EMAIL_PATTERN.matcher(emailParam).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = emailParam;
    }

    /**
     * Устанавливает описание пользователя.
     *
     * @param descriptionParam описание
     */
    public void setDescription(final String descriptionParam) {
        this.description = descriptionParam;
    }

    /**
     * Устанавливает список друзей.
     *
     * @param friendsParam список UID друзей
     * @throws IllegalArgumentException если UID недопустим
     */
    public void setFriends(final List<String> friendsParam) {
        if (friendsParam == null) {
            this.friends = new ArrayList<>();
            return;
        }

        for (String uid : friendsParam) {
            if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
                throw new IllegalArgumentException(
                        "Friend UID must be a valid UUID: " + uid);
            }
        }

        this.friends = new ArrayList<>(friendsParam);
    }

    /**
     * Добавляет UID друга.
     *
     * @param uidPrams UID друга
     * @throws IllegalArgumentException если UID недопустим
     */
    public void addFriend(final String uidPrams) {
        if (uidPrams == null || !UUID_PATTERN.matcher(uidPrams).matches()) {
            throw new IllegalArgumentException(
                    "Friend UID must be a valid UUID: " + uidPrams);
        }
        if (!friends.contains(uidPrams)) {
            friends.add(uidPrams);
        }
    }

    /**
     * Удаляет UID друга.
     *
     * @param uidParam UID друга
     * @throws IllegalArgumentException если UID недопустим
     */
    public void removeFriend(final String uidParam) {
        if (uidParam == null || !UUID_PATTERN.matcher(uidParam).matches()) {
            throw new IllegalArgumentException(
                    "Friend UID must be a valid UUID: " + uidParam);
        }
        friends.remove(uidParam);
    }
}
