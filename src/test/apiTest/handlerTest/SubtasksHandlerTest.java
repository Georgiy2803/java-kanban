package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubtasksHandlerTest {

    static managers.task.TaskManager taskManager = managers.Managers.getDefault();
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
    String time2 = "2025.07.12 11:15";
    String time3 = "2025.07.12 13:15";
    String time4 = "2025.07.12 13:15";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    LocalDateTime startTime2 = LocalDateTime.parse(time2, formatter);
    LocalDateTime startTime3 = LocalDateTime.parse(time3, formatter);
    LocalDateTime startTime4 = LocalDateTime.parse(time4, formatter);
    Duration duration = Duration.ofMinutes(30);


    Subtask subtask = new Subtask("name", "description", 2, Status.NEW, startTime, duration);
    Subtask subtask1 = new Subtask("name1", "description1", 1, startTime1, duration);
    Subtask subtask2 = new Subtask("name2", "description2", 3, Status.NEW, startTime, duration);
    Subtask subtask3 = new Subtask("name3", "description3", 1, startTime2, duration);
    Subtask subtask4 = new Subtask("name4", "description4", 1, startTime3, duration);
    Subtask subtask5 = new Subtask("name5", "description5", 5, startTime4, duration);

    Epic epic = new Epic("Epic", "description-Epic", 0);


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
    @Order(1) // Проверка создания подзадачи
    public void checkingTheAdditionSubtasks() throws IOException, InterruptedException, URISyntaxException {
        // Создаем эпик
        String requestBody = gson.toJson(epic);
        URI uri = new URI(baseUrl + "/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Статус ответа 201, была добавлена эпическая задача");
        // Создаем подзадачу
        String requestBody1 = gson.toJson(subtask1);
        URI uri1 = new URI(baseUrl + "/subtasks/");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody1))
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Статус ответа 201, была добавлена подзадача");
        // Проверяем состояние менеджера задач
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    @Order(2) // Проверка обновления подзадачи
    public void checkForUpdateSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response1 = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, подзадача " +
                "была обновлена, в Subtask указан id = 2.");
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    @Order(3) // проверьте обновить несуществующую подзадачу
    public void checkForUpdateToNonExistingSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask2);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, подзадача " +
                "добавлена не была, в Subtask указан id > 0, но обновлять не чего");
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    @Order(4) // проверка добавления пересекающейся подзадачи
    public void checkForAddAnIntersectionSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask3);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Cтатус ответа 406, добавляемая подзадача " +
                "пересекается по времени с другими задачами.");
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    @Order(5) // Проверка на добавление подзадачи с несуществующим идентификатором Эпика
    public void checkForAddSubtaskWithNonExistentIdEpic() throws IOException,
            InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask5);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Cтатус ответа 404, idEpic в добавляемой подзадаче " +
                "не соответствует id эпической задачи.");
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    @Order(6) // проверка получения идентификатора подзадачи
    public void checkGetIdSubtask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(subtask4);
        URI uri = new URI(baseUrl + "/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была " +
                "добавлена подзадача, в Subtask указан id = 0");
        assertEquals(2, taskManager.getSubtask().size());
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(1, taskManager.getHistory().size());

        URI uri1 = new URI(baseUrl + "/subtasks/3");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response1.statusCode(), "Cтатус ответа 200, " +
                "подзадача успешно выведена в теле ответа");
        assertEquals(2, taskManager.getSubtask().size());
        assertEquals(3, taskManager.getPrioritizedTasks().size());
        assertEquals(2, taskManager.getHistory().size());
        JsonElement jsonElement = JsonParser.parseString(response1.body());
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            assertEquals("name4", name, "Имя подзадачи должно совпадать");
            assertEquals("description4", description, "Описание подзадачи должно совпадать");
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент является массивом");
        }
    }

    @Test
    @Order(7) // проверка удаления подзадачи
    public void checkDeleteTask() throws IOException, InterruptedException, URISyntaxException {
        URI uri = new URI(baseUrl + "/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, подзадача с id = 2 была " +
                "успешно удалена");
        assertEquals(1, taskManager.getSubtask().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        assertEquals(2, taskManager.getHistory().size());
        String nameSubtask = taskManager.getSubtask().stream()
                .map(Task::getName)
                .findFirst()
                .orElse(null);

        assertEquals("name4", nameSubtask, "Удалена не верная задача");
    }
}
