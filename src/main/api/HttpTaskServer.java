package api;

import api.handler.*;
import api.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.task.TaskManager;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;
    private TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {

        this.taskManager = taskManager; // Сохраняем переданный объект TaskManager в поле класса
        gson = new GsonBuilder() // Инициализируем GsonBuilder для настройки сериализации JSON
                .setPrettyPrinting() // Включаем форматирование JSON для более читаемого вывода
                .serializeNulls() // Включаем сериализацию null-значений
                .registerTypeAdapter(Duration.class, new DurationAdapter()) // Регистрируем адаптер для класса Duration
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Регистрируем адаптер для класса LocalDateTime
                .create(); // Создаём экземпляр Gson с настроенными параметрами

        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0); // Создаём HTTP-сервер, который слушает указанный порт
            // Регистрируем маршруты для обработки запросов. gson — для сериализации и десериализации объектов в формате JSON
            httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) { // Выводим сообщение об ошибке, если не удалось создать сервер
            System.err.println("Ошибка при создании сервера: " + e.getMessage());
        }
    }

    public void startServer() { // запуск HTTP-сервера
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Managers.getDefault());
        server.startServer();// запускаем сервер
        System.out.println("HTTP-сервер запущен на порте " + PORT + "!");
        server.stopServer();// завершаем работу сервера
    }

}