package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.task.TaskManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import model.Epic;
import model.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SubtaskHandler.Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASKS_BY_ID:
                handleGetSubtaskId(exchange);
                break;
            case CREATE_SUBTASKS:
                handleCreateSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            case UNKNOWN:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private SubtaskHandler.Endpoint getEndpoint(URI uri, String method) {
        String[] path = uri.getPath().split("/"); // Разбиваем путь из URI на части по символу '/'

        switch (method) { // Используем switch для определения действия в зависимости от метода запроса
            case "GET": // Если запрос типа GET
                if (path.length >= 3 && path[2].matches("\\d+")) { // Проверяем, что путь включает id задачи
                    return SubtaskHandler.Endpoint.GET_SUBTASKS_BY_ID; // Возвращаем GET_TASK_BY_ID, если указан id задачи
                }
                return SubtaskHandler.Endpoint.GET_SUBTASKS; // Если id не указан, возвращаем GET_SUBTASKS (получение всех задач)

            case "POST": // Если запрос типа POST
                return SubtaskHandler.Endpoint.CREATE_SUBTASKS; // Всегда возвращаем CREATE_SUBTASKS (создание новой задачи)

            case "DELETE": // Если запрос типа DELETE
                if (path.length >= 3 && path[2].matches("\\d+")) { // Проверяем, что путь включает id задачи
                    return SubtaskHandler.Endpoint.DELETE_SUBTASK; // Возвращаем DELETE_SUBTASK, если указан id задачи
                }
                return SubtaskHandler.Endpoint.UNKNOWN; // Если id не указан, возвращаем UNKNOWN (неизвестный запрос)

            default: // Для всех остальных методов (PUT, PATCH и т.д.)
                return SubtaskHandler.Endpoint.UNKNOWN; // Возвращаем UNKNOWN (неизвестный запрос)
        }
    }

    enum Endpoint {
        GET_SUBTASKS,
        GET_SUBTASKS_BY_ID,
        CREATE_SUBTASKS,
        DELETE_SUBTASK,
        UNKNOWN
    }


    public void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента на вывод подзадач.");
        List<Subtask> subtasks = taskManager.getSubtask();
        sendText(httpExchange, gson.toJson(subtasks));
    }

    public void handleGetSubtaskId(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtask/{id} запроса от клиента на вывод подзадачи по её id.");
        int id = searchIdTask(httpExchange);
        Optional<Subtask> subtask = taskManager.getSubtaskById(id);
        if (!subtask.isPresent()) {
            sendNotFound(httpExchange, "Not Found");
        } else {
            String response = gson.toJson(subtask.get());
            sendText(httpExchange, response);
        }
    }

    public void handleCreateSubtask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента на создание подзадачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String subtaskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(subtaskString, Subtask.class);
        Integer idSubtask = subtask.getId() != null ? subtask.getId() : 0;

        if (idSubtask == 0) { //создание подзадачи
            Integer idSubtaskInEpic = subtask.getEpicId(); // вытаскиваем id Эпика к которому привязана подзадачи
            //Проверяем существует ли такой Эпик
            boolean isContainsIdEpic = false;
            for (Epic epic : taskManager.getEpics()) {
                if (epic.getId() == idSubtaskInEpic) {
                    isContainsIdEpic = true;
                    break;
                }
            }
            if (!isContainsIdEpic) { // если Эпик не был найден
                sendNotFound(httpExchange, "Эпическая задача с id указанном в подзадаче отсутствует");
            } else if (taskManager.createSubtask(subtask) != null) { // проверяем, была ли создана задача
                sendCreated(httpExchange, gson.toJson(subtask));
            } else {
                sendHasInteractions(httpExchange, "Not Acceptable");
            }
        } else { // обновление подзадачи
            // Проверяем существует ли такая подзадача
            boolean isContains = false;
            for (Subtask thisSubtask : taskManager.getSubtask()) {
                if (thisSubtask.getId() == idSubtask) {
                    isContains = true;
                    break;
                }
            }
            if (isContains) { // если такая подзадача существует
                taskManager.updateSubtask(subtask);
                String response = "Подзадача успешно обновлена";
                sendCreated(httpExchange, gson.toJson(response));
            } else {
                sendNotFound(httpExchange, "Подзадача с таким id в списках отсутствует");
            }
        }
    }

    public void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtask/{id} запроса от клиента на удаление подзадачи.");

        int id = searchIdTask(httpExchange);
        taskManager.deleteSubtaskById(id);
        sendText(httpExchange, "Subtask deleted");
    }
}