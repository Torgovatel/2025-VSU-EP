package ru.vsu.practice.demo;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Класс, представляющий пользователя системы.
 * Включает базовые поля, валидацию данных и операции управления друзьями.
 */
public class User {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z]+$");

    private static final Pattern UUID_PATTERN =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    private String id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String description;
    private List<String> friends = new ArrayList<>();

    /**
     * Конструктор по умолчанию, необходимый для корректной десериализации (Jackson).
     */
    public User() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Создает нового пользователя с автоматической генерацией уникального идентификатора.
     *
     * @param firstName   имя пользователя (только латинские буквы)
     * @param lastName    фамилия пользователя (только латинские буквы)
     * @param age         возраст (должен быть не меньше 12 лет)
     * @param email       электронная почта в валидном формате
     * @param description описание пользователя
     * @param friends     список UID друзей (должны быть корректными UUID)
     * @throws IllegalArgumentException при нарушении валидации
     */
    public User(String firstName, String lastName, int age, String email,
                String description, List<String> friends) throws IllegalArgumentException {
        this.id = UUID.randomUUID().toString();
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setEmail(email);
        setDescription(description);
        setFriends(friends);
    }

    public User create(User user) {
        if (user.id == null) {
            user.id = UUID.randomUUID().toString();
        }
        return user;
    }

    /**
     * @return Уникальный идентификатор пользователя (UUID).
     */
    public String getId() {
        return id;
    }

    /**
     * @return Имя пользователя.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return Фамилия пользователя.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return Возраст пользователя.
     */
    public int getAge() {
        return age;
    }

    public Integer GetAge() {return age;}
    /**
     * @return Адрес электронной почты.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return Описание пользователя.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Список UID друзей пользователя.
     */
    public List<String> getFriends() {
        return new ArrayList<String>(friends);
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя (только буквы)
     * @throws IllegalArgumentException если имя некорректное
     */
    public void setFirstName(String firstName) throws IllegalArgumentException {
        if (firstName == null || !NAME_PATTERN.matcher(firstName).matches()) {
            throw new IllegalArgumentException("First name must contain only letters.");
        }
        this.firstName = firstName;
    }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия (только буквы)
     * @throws IllegalArgumentException если фамилия некорректная
     */
    public void setLastName(String lastName) throws IllegalArgumentException {
        if (lastName == null || !NAME_PATTERN.matcher(lastName).matches()) {
            throw new IllegalArgumentException("Last name must contain only letters.");
        }
        this.lastName = lastName;
    }
    public void setId(String uid) {
        this.id = uid;
    }

    /**
     * Устанавливает возраст пользователя.
     *
     * @param age возраст (не менее 12 лет)
     * @throws IllegalArgumentException если возраст меньше 12 лет
     * @see <a href="https://example.com/age-policy">Возрастная политика сайта</a>
     */
    public void setAge(int age) throws IllegalArgumentException {
        if (age < 12) {
            throw new IllegalArgumentException("Age must be at least 12. See site age policy.");
        }
        this.age = age;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email адрес электронной почты
     * @throws IllegalArgumentException если email в неправильном формате
     */
    public void setEmail(String email) throws IllegalArgumentException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        this.email = email;
    }

    /**
     * Устанавливает описание профиля пользователя.
     *
     * @param description описание
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Устанавливает список друзей по UID.
     *
     * @param friends список UID (должны быть валидными UUID)
     * @throws IllegalArgumentException если в списке есть невалидные UID
     */
    public void setFriends(List<String> friends) throws IllegalArgumentException {
        if (friends == null) {
            this.friends = new ArrayList<String>();
            return;
        }

        for (String uid : friends) {
            if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
                throw new IllegalArgumentException("Friend UID must be a valid UUID: " + uid);
            }
        }

        this.friends = new ArrayList<String>(friends);
    }

    /**
     * Добавляет друга по UID в список, если его там ещё нет.
     *
     * @param uid UID друга
     * @throws IllegalArgumentException если UID невалидный
     */
    public void addFriend(String uid) throws IllegalArgumentException {
        if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
            throw new IllegalArgumentException("Friend UID must be a valid UUID: " + uid);
        }
        if (!friends.contains(uid)) {
            friends.add(uid);
        }
    }

    /**
     * Удаляет друга по UID из списка.
     *
     * @param uid UID друга
     * @throws IllegalArgumentException если UID невалидный
     */
    public void removeFriend(String uid) throws IllegalArgumentException {
        if (uid == null || !UUID_PATTERN.matcher(uid).matches()) {
            throw new IllegalArgumentException("Friend UID must be a valid UUID: " + uid);
        }
        friends.remove(uid);
    }
}
