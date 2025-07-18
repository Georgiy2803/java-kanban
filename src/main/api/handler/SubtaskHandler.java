package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectionException;
import exception.NotFoundException;
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
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        URI requestURI = httpExchange.getRequestURI();
        String path = requestURI.getPath();
        String[] splitString = path.split("/");

        try {
            switch (method) {
                case "GET":
                    if (splitString.length == 2) {
                        handleGetSubtasks(httpExchange);
                    } else {
                        handleGetSubtaskById(httpExchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateSubtask(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteSubtask(httpExchange);
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

    public void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на вывод всех подзадач.");
        List<Subtask> subtasks = taskManager.getSubtask();
        sendText(httpExchange, gson.toJson(subtasks));
    }

    public void handleGetSubtaskById(HttpExchange httpExchange) throws IOException, NotFoundException {
        System.out.println("Обработка запроса на вывод подзадачи по id.");
        int id = searchIdTask(httpExchange);
        Subtask subtask = taskManager.getSubtaskById(id).orElseThrow(() ->
                new NotFoundException("Подзадача с указанным ID не найдена"));
        sendText(httpExchange, gson.toJson(subtask));
    }

    public void handleCreateOrUpdateSubtask(HttpExchange httpExchange) throws IOException, IntersectionException, NotFoundException {
        System.out.println("Обработка запроса на создание или обновление подзадачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String subtaskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(subtaskString, Subtask.class);

        if (subtask.getId() == null || subtask.getId() == 0) {
            // Создание новой подзадачи
            Integer idSubtaskInEpic = subtask.getEpicId(); // вытаскиваем id Эпика к которому привязана подзадача
            // Проверяем, существует ли такой Эпик
            Optional<Epic> epic = taskManager.getEpicById(idSubtaskInEpic);
            if (!epic.isPresent()) {
                sendNotFound(httpExchange, "Эпическая задача с указанным ID не найдена");
            } else {
                taskManager.createSubtask(subtask);
                sendCreated(httpExchange, gson.toJson(subtask));
            }
        } else {
            // Обновление существующей подзадачи
            taskManager.updateSubtask(subtask);
            sendOkUpdate(httpExchange, gson.toJson("Подзадача успешно обновлена"));
        }
    }

    public void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на удаление подзадачи.");
        int id = searchIdTask(httpExchange);
        taskManager.deleteSubtaskById(id);
        sendText(httpExchange, "Подзадача успешно удалена");
    }

}