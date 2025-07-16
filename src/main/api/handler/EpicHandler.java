package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import managers.task.TaskManager;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC:
                handleGetEpicId(exchange);
                break;
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case POST_EPIC:
                handleCreateEpic(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case UNKNOWN:
                sendMethodNotAllowed(exchange);
                break;
        }
    }

    private Endpoint getEndpoint(URI uri, String method) {
        String[] path = uri.getPath().split("/");

        switch (method) {
            case "GET":
                if (path.length == 2) {
                    return Endpoint.GET_EPICS;
                } else if (path.length == 3) {
                    return Endpoint.GET_EPIC;
                } else if (path.length == 4 && path[3].equals("subtasks")) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            case "POST":
                return Endpoint.POST_EPIC;
            case "DELETE":
                return (path.length > 2 ? Endpoint.DELETE_EPIC : Endpoint.UNKNOWN);
            default:
                return Endpoint.UNKNOWN;

        }
    }

    enum Endpoint {
        GET_EPIC,
        GET_EPICS,
        GET_EPIC_SUBTASKS,
        POST_EPIC,
        DELETE_EPIC,
        UNKNOWN
    }

    public void handleGetEpics(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента на вывод эпических задач.");
        List<Epic> epics = taskManager.getEpics();// Получаем список задач из TaskManager
        sendText(httpExchange, gson.toJson(epics)); // Преобразуем список задач в JSON и отправляем ответ клиенту
    }

    public void handleGetEpicId(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id} запроса от клиента на вывод эпической задачи по её id.");
        int id = searchIdTask(httpExchange);
        Optional<Epic> epic = taskManager.getEpicById(id);
        if (!epic.isPresent()) {
            sendNotFound(httpExchange, "Not Found");
        } else {
            sendText(httpExchange, gson.toJson(epic.get()));
        }
    }

    public void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id}/subtasks запроса от клиента на вывод подзадач " +
                "входящих в эпическую задачу по id эпической задачи.");
        int id = searchIdTask(httpExchange);
        Optional<Epic> epic = taskManager.getEpicById(id);
        if (!epic.isPresent()) {
            sendNotFound(httpExchange, "Not Found");
        } else {
            List<Subtask> subtask = taskManager.listOfEpicSubtasks(id);
            sendText(httpExchange, gson.toJson(subtask));
        }
    }

    public void handleCreateEpic(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента на создание эпической задачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String epicString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicString, Epic.class);
        Integer idEpic = epic.getId() != null ? epic.getId() : 0;

        // создание Эпик
        if (idEpic == 0) {
            if (taskManager.createEpic(epic) != null) {
                sendCreated(httpExchange, gson.toJson(epic));
            } else {
                sendHasInteractions(httpExchange, "Not Acceptable");
            }
        } else { // Обновление Эпик
            boolean isContains = taskManager.getEpics().stream()
                    .map(Epic::getId)
                    .anyMatch(i -> i == idEpic);
            if (!isContains) {
                sendNotFound(httpExchange, "Эпическая задача с таким id в списках отсутствует");
            } else {
                taskManager.updateEpic(epic);
                sendCreated(httpExchange, gson.toJson("Эпическая задача успешно обновлена"));
            }
        }
    }

    public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics/{id} запроса от клиента на удаление эпической задачи.");

        int id = searchIdTask(httpExchange);
        taskManager.deleteEpicById(id);
        sendText(httpExchange, "Epic deleted");
    }
}