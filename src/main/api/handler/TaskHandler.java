package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
    public void handle(HttpExchange exchange) throws IOException {
        TaskHandler.Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK_BY_ID:
                handleGetTaskById(exchange);
                break;
            case CREATE_TASK:
                handleCreateTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            case UNKNOWN:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private TaskHandler.Endpoint getEndpoint(URI uri, String method) {
        String[] path = uri.getPath().split("/"); // Разбиваем путь из URI на части по символу '/'

        switch (method) { // Используем switch для определения действия в зависимости от метода запроса
            case "GET": // Если запрос типа GET
                if (path.length >= 3 && path[2].matches("\\d+")) { // Проверяем, что путь включает id задачи
                    return TaskHandler.Endpoint.GET_TASK_BY_ID; // Возвращаем GET_TASK_BY_ID, если указан id задачи
                }
                return TaskHandler.Endpoint.GET_TASKS; // Если id не указан, возвращаем GET_TASKS (получение всех задач)

            case "POST": // Если запрос типа POST
                return TaskHandler.Endpoint.CREATE_TASK; // Всегда возвращаем CREATE_TASK (создание новой задачи)

            case "DELETE": // Если запрос типа DELETE
                if (path.length >= 3 && path[2].matches("\\d+")) { // Проверяем, что путь включает id задачи
                    return TaskHandler.Endpoint.DELETE_TASK; // Возвращаем DELETE_TASK, если указан id задачи
                }
                return TaskHandler.Endpoint.UNKNOWN; // Если id не указан, возвращаем UNKNOWN (неизвестный запрос)

            default: // Для всех остальных методов (PUT, PATCH и т.д.)
                return TaskHandler.Endpoint.UNKNOWN; // Возвращаем UNKNOWN (неизвестный запрос)
        }
    }

    enum Endpoint {
        GET_TASKS,
        GET_TASK_BY_ID,
        CREATE_TASK,
        DELETE_TASK,
        UNKNOWN
    }

    public void handleGetTasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на вывод всех задач.");// Печать в консоль сообщения о начале обработки запроса на вывод всех задач
        List<Task> tasks = taskManager.getTasks();// Получаем список всех задач из TaskManager
        sendText(httpExchange, gson.toJson(tasks));// Преобразуем список задач в JSON и отправляем клиенту
    }

    public void handleGetTaskById(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на вывод задачи по id.");// Печать в консоль сообщения о начале обработки запроса на вывод задачи по id
        int id = searchIdTask(httpExchange); // Получаем id задачи из запроса
        Optional<Task> task = taskManager.getTaskById(id); // Получаем задачу по id из TaskManager
        if (!task.isPresent()) { // Если задача не найдена, отправляем ответ с кодом 404 (Not Found)
            sendNotFound(httpExchange, "Задача не найдена");
        } else {
            sendText(httpExchange, gson.toJson(task.get())); // Преобразуем найденную задачу в JSON и отправляем клиенту
        }
    }

    public void handleCreateTask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента на создание задачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(taskString, Task.class);
        Integer idTask = task.getId() != null ? task.getId() : 0;

        //создание задачи
        if (idTask == 0) {
            if (taskManager.createTask(task) != null) {
                sendCreated(httpExchange, gson.toJson(task));
            } else {
                sendHasInteractions(httpExchange, "Not Acceptable");
            }
        } else { // обновление задачи
            boolean isContains = taskManager.getTasks().stream()
                    .map(Task::getId)
                    .anyMatch(i -> i == idTask);
            if (!isContains) {
                sendNotFound(httpExchange, "Задачи с таким id в списках отсутствует");
            } else {
                taskManager.updateTask(task);
                sendCreated(httpExchange, gson.toJson("Задача успешно обновлена"));
            }
        }
    }

    public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на удаление задачи.");// Печать в консоль сообщения о начале обработки запроса на удаление задачи
        int id = searchIdTask(httpExchange);
        taskManager.deleteTaskById(id);// Удаляем задачу по id через TaskManager
        sendText(httpExchange, "Задача успешно удалена");// Отправляем ответ с текстом "Задача успешно удалена"
    }

}