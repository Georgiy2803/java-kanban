package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import com.google.gson.Gson;
import managers.task.TaskManager;
import model.Task;
import java.util.List;


public class HistoryHandler extends BaseHttpHandler implements HttpHandler{

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /history запроса от клиента на вывод истории просмотров.");

        try {
            List<Task> tasks = taskManager.getHistory();
            sendText(httpExchange, gson.toJson(tasks));
        } catch (IOException e) {
            sendInternalServerError(httpExchange, "Internal Server Error");
        }
    }

}

