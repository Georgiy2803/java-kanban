package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectionException;
import exception.NotFoundException;
import managers.task.TaskManager;
import model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitString = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (splitString.length == 2) {
                        handleGetTasks(httpExchange);
                    } else {
                        handleGetTaskById(httpExchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateTask(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteTask(httpExchange);
                    break;
                default:
                    sendMethodNotAllowed(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IntersectionException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(httpExchange, "Internal Server Error");
        }
    }

    public void handleGetTasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на вывод всех задач.");// Печать в консоль сообщения о начале обработки запроса на вывод всех задач
        List<Task> tasks = taskManager.getTasks();// Получаем список всех задач из TaskManager
        sendText(httpExchange, gson.toJson(tasks));// Преобразуем список задач в JSON и отправляем клиенту
    }

    public void handleGetTaskById(HttpExchange httpExchange) throws IOException, NotFoundException {
        System.out.println("Обработка запроса на вывод задачи по id.");// Печать в консоль сообщения о начале обработки запроса на вывод задачи по id
        int id = searchIdTask(httpExchange); // Получаем id задачи из запроса
        Optional<Task> task = taskManager.getTaskById(id); // Получаем задачу по id из TaskManager
        if (!task.isPresent()) { // Если задача не найдена, отправляем ответ с кодом 404 (Not Found)
            sendNotFound(httpExchange, "Задача не найдена");
        } else {
            sendText(httpExchange, gson.toJson(task.get())); // Преобразуем найденную задачу в JSON и отправляем клиенту
        }
    }

    public void handleCreateOrUpdateTask(HttpExchange httpExchange) throws IOException, IntersectionException, NotFoundException {
        System.out.println("Обработка запроса на создание или обновление задачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskString, Task.class);

        // Определяем, это создание или обновление задачи
        if (task.getId() == null || task.getId() == 0) {
            // Создание новой задачи
            taskManager.createTask(task);
            sendCreated(httpExchange, gson.toJson(task));
        } else {
            // Обновление существующей задачи
            taskManager.updateTask(task); // обновляем задачу
            sendOkUpdate(httpExchange, gson.toJson("Задача успешно обновлена"));
        }
    }

    public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на удаление задачи.");// Печать в консоль сообщения о начале обработки запроса на удаление задачи
        int id = searchIdTask(httpExchange);
        taskManager.deleteTaskById(id);// Удаляем задачу по id через TaskManager
        sendText(httpExchange, "Задача успешно удалена");// Отправляем ответ с текстом "Задача успешно удалена"
    }

}