package managers;

import managers.Managers;
import managers.history.HistoryManager;
import managers.task.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void getDefault_returnNotEmpty() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager); // Проверяем, что класс не null
    }

    @Test
    void getDefaultHistory_returnNotEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager); // Проверяем, что класс не null
    }

}