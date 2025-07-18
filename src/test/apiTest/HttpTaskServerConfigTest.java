package apiTest;

import api.HttpTaskServer;
import managers.Managers;
import managers.task.TaskManager;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.*;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerConfigTest {

    static TaskManager taskManager = Managers.getDefault(); // Менеджер задач, используемый в тестах
    static HttpTaskServer server = new HttpTaskServer(taskManager); // Сервер для тестирования
    static HttpClient httpClient = HttpClient.newHttpClient(); // Клиент для отправки HTTP-запросов

    String baseUrl = "http://localhost:8080"; // Адрес нашего сервера

    @BeforeAll
    static void start() {
        server.startServer(); // Запускаем сервер перед началом тестов
    }

    @AfterAll
    public static void stop() {
        server.stopServer(); // Останавливаем сервер после завершения всех тестов
    }

    @Test
    void shouldObjectOfInMemoryTaskManagerClassMustBeCreated() {
        // Проверяем, что созданный объект server является экземпляром класса HttpTaskServer
        assertInstanceOf(HttpTaskServer.class, server, "Созданный объект не является объектом класса HttpTaskServer");
    }

    @Test
    void checkHandlerTasks() throws URISyntaxException, IOException, InterruptedException {
        // Проверяем работоспособность обработчика задач (/tasks/)
        URI uri = new URI(baseUrl + "/tasks/"); // Создаем URI для запроса ко всем задачам
        HttpRequest request = HttpRequest.newBuilder()// Строим HTTP-запрос GET-методом с указанием URI и заголовка Accept для JSON
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString()); // Отправляем запрос и получаем ответ в виде строки
        assertEquals(200, response.statusCode(), "Статус ответа должен быть равен 200");// Проверяем, что статус ответа равен 200 (OK)
        String body = response.body();// Получаем тело ответа
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");// Проверяем, что тело ответа содержит JSON-массив (начинается с символа '[')
    }

    @Test
    void checkHandlerEpics() throws URISyntaxException, IOException, InterruptedException {
        // Проверяем работоспособность обработчика эпиков (/epics/)
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус ответа должен быть равен 200");
        String body = response.body();
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerSubtasks() throws URISyntaxException, IOException, InterruptedException {
        // Проверяем работоспособность обработчика подзадач (/subtasks/)
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус ответа должен быть равен 200");
        String body = response.body();
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerHistory() throws URISyntaxException, IOException, InterruptedException {
        // Проверяем работоспособность обработчика истории просмотра задач (/history/)
        URI uri = new URI(baseUrl + "/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус ответа должен быть равен 200");
        String body = response.body();
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }

    @Test
    void checkHandlerPrioritized() throws URISyntaxException, IOException, InterruptedException {
        // Проверяем работоспособность обработчика приоритизированных задач (/prioritized/)
        URI uri = new URI(baseUrl + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Статус ответа должен быть равен 200");
        String body = response.body();
        assertTrue(body.contains("["), "Проверяем, что это JSON массив");
    }
}