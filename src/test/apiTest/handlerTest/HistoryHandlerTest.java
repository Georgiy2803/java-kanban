package apiTest.handlerTest;

import api.HttpTaskServer;
import exception.IntersectionException;
import exception.NotFoundException;
import managers.Managers;
import managers.task.TaskManager;
import model.Status;
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

public class HistoryHandlerTest {

    static TaskManager taskManager = Managers.getDefault();
    static HttpTaskServer server = new HttpTaskServer(taskManager);
    static HttpClient httpClient = HttpClient.newHttpClient();

    String baseUrl = "http://localhost:8080";
    String time = "2025.06.14 14:00";
    String time1 = "2025.06.14 15:00";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    LocalDateTime startTime = LocalDateTime.parse(time, formatter);
    LocalDateTime startTime1 = LocalDateTime.parse(time1, formatter);
    Duration duration = Duration.ofMinutes(15);

    Task task1 = new Task("name1", "description1", 0, Status.NEW, startTime, duration);
    Task task2 = new Task("name2", "description2", 0, Status.NEW, startTime1, duration);

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
    public void checkingHistoryTask() throws IOException, InterruptedException, URISyntaxException, NotFoundException, IntersectionException {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        URI uri = new URI(baseUrl + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        System.out.println(uri);
        System.out.println(request);

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Cтатус ответа 200, была " +
                "запрошена история просмотров задач");
        assertEquals(2, taskManager.getHistory().size());
    }
}
