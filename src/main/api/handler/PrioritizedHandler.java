package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import managers.task.TaskManager;
import com.google.gson.Gson;
import model.Task;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler{

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /prioritized запроса от клиента на вывод списка отсортированных задач.");
        try {
            List<Task> prioritized = taskManager.getPrioritizedTasks();
            sendText(httpExchange, gson.toJson(prioritized));
        } catch (IOException e) {
            sendInternalServerError(httpExchange, "Internal Server Error");
        }
    }

}
