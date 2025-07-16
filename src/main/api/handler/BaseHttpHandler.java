package api.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import managers.task.TaskManager;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }


    // для отправки общего ответа в случае успеха
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8); // Преобразуем строку text в массив байтов с использованием кодировки UTF-8
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");// Устанавливаем заголовок Content-Type для ответа, указывая, что данные будут в формате JSON с кодировкой UTF-8
        exchange.sendResponseHeaders(200, response.length);// Отправляем заголовки ответа с кодом состояния 200 (OK) и длиной содержимого равной длине массива байтов response
        try (OutputStream os = exchange.getResponseBody()) { // Используем try-with-resources для автоматического закрытия OutputStream после записи данных
            os.write(response); // Записываем массив байтов response в выходной поток
        }
    }

    // отправка в порядке
    protected void sendOk(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);// Отправляем заголовки ответа с кодом состояния 200 (OK) и длиной содержимого 0
        exchange.close();// Закрываем соединение
    }

    // отправка неверного запроса.
    protected void sendBadRequest(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);// Преобразуем строку text в массив байтов с использованием кодировки UTF-8
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");// Устанавливаем заголовок Content-Type для ответа, указывая, что данные будут в формате plain text с кодировкой UTF-8
        exchange.sendResponseHeaders(400, response.length);// Отправляем заголовки ответа с кодом состояния 400 (Bad Request) и длиной содержимого равной длине массива байтов response
        try (OutputStream os = exchange.getResponseBody()) {// Используем try-with-resources для автоматического закрытия OutputStream после записи данных
            os.write(response);// Записываем массив байтов response в выходной поток
        }
    }

    // для отправки ответа в случае, если объект не был найден
    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        exchange.sendResponseHeaders(404, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    // sendHasOverlaps - для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
    protected void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        exchange.sendResponseHeaders(406, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    // отправка создана
    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);// Преобразуем строку text в массив байтов с использованием кодировки UTF-8
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");// Устанавливаем заголовок Content-Type для ответа, указывая, что данные будут в формате JSON с кодировкой UTF-8
        exchange.sendResponseHeaders(201, response.length);// Отправляем заголовки ответа с кодом состояния 201 (Created) и длиной содержимого равной длине массива байтов response
        try (OutputStream os = exchange.getResponseBody()) {// Используем try-with-resources для автоматического закрытия OutputStream после записи данных
            os.write(response); // Записываем массив байтов response в выходной поток
        }
    }

    // отправка Внутренней ошибки Сервера
    protected void sendInternalServerError(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        exchange.sendResponseHeaders(500, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    // отправка не разрешена
    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(405, 0);
        exchange.close();
    }

    //Задача поиска идентификатора
    protected int searchIdTask(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (parts.length < 3) {
            throw new IllegalArgumentException("Некорректный путь в запросе");
        }
        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Не удалось преобразовать идентификатор задачи в число");
        }
    }


}