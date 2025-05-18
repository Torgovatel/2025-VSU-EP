package ru.vsu.practice.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public final class HelloHtmlController {

    /** HTML-содержимое, загружаемое из ресурса Hello.html. */
    private final String htmlContent;

    /**
     * Конструктор, загружает HTML из ресурса.
     */
    public HelloHtmlController() {
        this.htmlContent = loadHtmlFromFile();
    }

    /**
     * Обрабатывает GET-запрос по корневому URL и возвращает HTML.
     *
     * @return содержимое Hello.html
     */
    @GetMapping("/")
    public String serveHelloHtml() {
        return htmlContent;
    }

    private String loadHtmlFromFile() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("Hello.html")) {
            if (is == null) {
                throw new RuntimeException("Resource Hello.html not found");
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "<h1>Ошибка загрузки HELLO.html</h1><p>"
                    + e.getMessage() + "</p>";
        }
    }
}
