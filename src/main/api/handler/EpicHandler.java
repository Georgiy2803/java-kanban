package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.IntersectionException;
import exception.NotFoundException;
import model.Epic;
import model.Subtask;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import managers.task.TaskManager;


public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
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
                        handleGetEpics(httpExchange);
                    } else if (splitString.length == 3) {
                        handleGetEpicById(httpExchange);
                    } else if (splitString.length == 4 && splitString[3].equals("subtasks")) {
                        handleGetEpicSubtasks(httpExchange);
                    }
                    break;
                case "POST":
                    handleCreateOrUpdateEpic(httpExchange);
                    break;
                case "DELETE":
                    handleDeleteEpic(httpExchange);
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

    public void handleGetEpics(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на вывод всех эпических задач.");
        List<Epic> epics = taskManager.getEpics();
        sendText(httpExchange, gson.toJson(epics));
    }

    public void handleGetEpicById(HttpExchange httpExchange) throws IOException, NotFoundException {
        System.out.println("Обработка запроса на вывод эпической задачи по id.");
        int id = searchIdTask(httpExchange);
        Epic epic = taskManager.getEpicById(id).orElseThrow(() ->
                new NotFoundException("Эпическая задача с указанным ID не найдена"));
        sendText(httpExchange, gson.toJson(epic));
    }

    public void handleGetEpicSubtasks(HttpExchange httpExchange) throws IOException, NotFoundException {
        System.out.println("Обработка запроса на вывод подзадач, входящих в эпическую задачу по id.");
        int id = searchIdTask(httpExchange);
        Epic epic = taskManager.getEpicById(id).orElseThrow(() ->
                new NotFoundException("Эпическая задача с указанным ID не найдена"));
        List<Subtask> subtasks = taskManager.listOfEpicSubtasks(id);
        sendText(httpExchange, gson.toJson(subtasks));
    }

    public void handleCreateOrUpdateEpic(HttpExchange httpExchange) throws IOException, IntersectionException, NotFoundException {
        System.out.println("Обработка запроса на создание или обновление эпической задачи.");
        InputStream inputStream = httpExchange.getRequestBody();
        String epicString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(epicString, Epic.class);

        if (epic.getId() == null || epic.getId() == 0) {
            // Создание новой эпической задачи
            taskManager.createEpic(epic);
            sendCreated(httpExchange, gson.toJson(epic));
        } else {
            // Обновление существующей эпической задачи
            taskManager.updateEpic(epic);
            sendOkUpdate(httpExchange, gson.toJson("Эпическая задача успешно обновлена"));
        }
    }

    public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        System.out.println("Обработка запроса на удаление эпической задачи.");
        int id = searchIdTask(httpExchange);
        taskManager.deleteEpicById(id);
        sendText(httpExchange, "Эпическая задача успешно удалена");
    }

}