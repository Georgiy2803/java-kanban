package apiTest.handlerTest;

import api.HttpTaskServer;
import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import managers.Managers;
import managers.task.TaskManager;
import model.Status;
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
import com.google.gson.Gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TasksHandlerTest {

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
    String time = "2025-07-12T11:00"; // Используем ISO-формат даты
    String time1 = "2025-07-12T12:00";
    String time2 = "2025-07-12T13:15";

    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    LocalDateTime startTime2 = LocalDateTime.parse(time2, formatter);
    Duration duration = Duration.ofMinutes(30);

    Task task1 = new Task("name1", "description1", 1, Status.NEW, startTime, duration);
    Task task2 = new Task("name2", "description2", 0, Status.NEW, startTime1, duration);
    Task task3 = new Task("name3", "description3", 3, Status.NEW, startTime, duration);
    Task task4 = new Task("name4", "description4", 0, Status.NEW, startTime2, duration);

    @BeforeAll
    static void start() {
        server.startServer(); // Запускаем сервер перед всеми тестами
        taskManager.deleteAllEpic(); // Очищаем все задачи перед каждым тестом
        taskManager.deleteAllTasks();
    }

    @AfterAll
    public static void stop() {
        server.stopServer(); // Останавливаем сервер после всех тестов
    }

    @Test
    @Order(1) // проверка добавления задачи
    public void checkAddTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task2); // Преобразуем объект task2 в JSON-строку
        URI uri = new URI(baseUrl + "/tasks/");// Создаем URI для обращения к ресурсу "/tasks/"
        HttpRequest request = HttpRequest.newBuilder() // Создаем HTTP-запрос типа POST с указанием тела запроса
                .uri(uri)  // Указываем URI
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // Устанавливаем тело запроса
                .build();  // Собираем запрос

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());// Отправляем запрос и получаем ответ от сервера
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была добавлена задача с id = 0");// Проверяем, что сервер ответил статусом 201 (CREATED)
        assertEquals(1, taskManager.getTasks().size(), "Размер списка задач должен быть 1");// Проверяем, что в менеджере задач появился одна задача
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно быть 1");// Проверяем, что среди приоритетных задач появилась одна задача
        assertEquals(0, taskManager.getHistory().size(), "История задач должна быть пуста");// Проверяем, что история задач пуста (так как задача только добавлена)
    }

    @Test
    @Order(2) // проверка обновления задачи с правильным id
    public void checkUpdateTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task1);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Cтатус ответа 201, задача была обновлена");
        assertEquals(1, taskManager.getTasks().size(), "Размер списка задач должен оставаться 1");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно остаться 1");
        assertEquals(0, taskManager.getHistory().size(), "История задач должна быть пуста");
    }

    @Test
    @Order(3) // проверка обновления задачи c неверно указанным id
    public void checkingForUpdatesToNonExistingTask() throws IOException, InterruptedException, URISyntaxException {
        String requestBody = gson.toJson(task3);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Cтатус ответа 404, задача не была обновлена, так как id > 0 и задача не существует");
        assertEquals(1, taskManager.getTasks().size(), "Размер списка задач должен оставаться 1");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно остаться 1");
        assertEquals(0, taskManager.getHistory().size(), "История задач должна быть пуста");
    }

    @Test
    @Order(4) // Проверяем получение задачи по её id
    public void checkingGetIdTask() throws IOException, InterruptedException, URISyntaxException {

        String requestBody = gson.toJson(task4);
        URI uri = new URI(baseUrl + "/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        System.out.println(taskManager.getTasks());
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasks());
        assertEquals(201, response.statusCode(), "Cтатус ответа 201, была добавлена задача с id = 0");
        assertEquals(2, taskManager.getTasks().size(), "Размер списка задач должен увеличиться до 2");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно увеличиться до 2");
        assertEquals(0, taskManager.getHistory().size(), "История задач должна быть пуста");

        URI uri1 = new URI(baseUrl + "/tasks/2");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(uri1)
                .GET()
                .build();
        HttpResponse<String> response1 = httpClient.send(request1, HttpResponse.BodyHandlers.ofString());
        System.out.println(taskManager.getTasks());
        assertEquals(200, response1.statusCode(), "Cтатус ответа 200, задача успешно получена");
        assertEquals(2, taskManager.getTasks().size(), "Размер списка задач должен оставаться 2");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно оставаться 2");
        assertEquals(1, taskManager.getHistory().size(), "История задач должна содержать 1 элемент");

        // Проверяем, что ответ содержит JSON-объект с именем и описанием задачи
        // Используем JsonParser для анализа строки JSON, полученной из response1.body(), и сохраняем результат в переменную jsonElement
        JsonElement jsonElement = JsonParser.parseString(response1.body());
        if (jsonElement.isJsonObject()) { // Проверяем, является ли jsonElement объектом JSON
            JsonObject jsonObject = jsonElement.getAsJsonObject();// Если да, преобразуем его в JsonObject и сохраняем в переменную jsonObject
            String name = jsonObject.get("name").getAsString();// Извлекаем значение ключа "name" из jsonObject и преобразуем его в строку, сохраняя в переменную name
            String description = jsonObject.get("description").getAsString();// Аналогично извлекаем значение ключа "description" и сохраняем в переменную description
            assertEquals("name4", name, "Имя задачи должно соответствовать ожиданию");// Проверяем, равно ли значение name строке "name4", и выводим сообщение, если это не так
            assertEquals("description4", description, "Описание задачи должно соответствовать ожиданию");// Аналогично проверяем значение description
        } else {
            assertInstanceOf(JsonArray.class, jsonElement, "Элемент должен быть массивом");// Если jsonElement не является объектом JSON, проверяем, является ли он массивом JSON, и выводим сообщение в противном случае
        }
    }

    @Test
    @Order(5) // Проверяем удаление задачи по её id
    public void checkingDeleteTask() throws IOException, InterruptedException, URISyntaxException {

        URI uri = new URI(baseUrl + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, задача с id = 1 была успешно удалена");
        assertEquals(1, taskManager.getTasks().size(), "Размер списка задач должен уменьшиться до 1");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Количество приоритетных задач должно уменьшиться до 1");
        assertEquals(1, taskManager.getHistory().size(), "История задач должна содержать 1 элемент");
        String nameTask = taskManager.getTasks().stream()
                .map(task -> task.getName())
                .findFirst()
                .orElse(null);
        assertEquals("name4", nameTask, "Удалена не верная задача");
    }
}
