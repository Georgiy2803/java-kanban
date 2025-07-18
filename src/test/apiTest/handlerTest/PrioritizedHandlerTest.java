package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import managers.Managers;
import managers.task.TaskManager;
import model.Status;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrioritizedHandlerTest {
    static TaskManager taskManager = Managers.getDefault();
    static HttpTaskServer server = new HttpTaskServer(taskManager);
    static HttpClient httpClient = HttpClient.newHttpClient();
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    String baseUrl = "http://localhost:8080";
    String time = "2025.07.12 11:00";
    String time1 = "2025.07.12 12:00";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    Duration duration1 = Duration.ofMinutes(30);
    Duration duration2 = Duration.ofMinutes(0);


    Task task1 = new Task("name1", "description1", 0, Status.NEW, startTime1, duration1);
    Task task2 = new Task("name2", "description2", 0, Status.NEW, null, duration2);

    @BeforeAll
    static void start() {
        server.startServer();
        taskManager.deleteAllEpic(); // Очищаем все задачи перед каждым тестом
        taskManager.deleteAllTasks();
    }

    @AfterAll
    public static void stop() {
        server.stopServer();
    }

    @Test
    @Order(1) // проверка Приоритетности Добавления Задачи С Пустым Временем Заполнения Поля
    public void checkingPrioritizedAddingTaskWithEmptyFieldTime() throws IOException, InterruptedException, URISyntaxException {

        String requestBody = gson.toJson(task1);

        URI uri = new URI(baseUrl + "/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была добавлена задача");
        assertEquals(1, taskManager.getTasks().size());
        URI uri1 = new URI(baseUrl + "/prioritized");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());

        JsonElement jsonElement = JsonParser.parseString(response1.body());
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            assertEquals("name1", name, "Имена задач должны совпадать");
            assertEquals("description1", description, "Описание задач должны совпадать");
            assertEquals(1, taskManager.getPrioritizedTasks().size());
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент является массивом");
        }
    }

    @Test
    @Order(2)
    public void checkingPrioritizedAddingTaskWithEmptyFieldsDurationAndTime() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task2);
        URI uri = new URI(baseUrl + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                    "добавлена задача, в Task указан id = 0");
            assertEquals(2, taskManager.getTasks().size(), "Задача должна попасть в список задач");
            assertEquals(1, taskManager.getPrioritizedTasks().size(), "Задача НЕ должна попасть в" +
                    " приоритетный список задач");
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
