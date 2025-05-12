package test.managers;

import main.managers.Managers;
import main.managers.history.HistoryManager;
import main.managers.task.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void CheckGetDefault_isNotEmpty() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager); // Проверяем, что класс не null
    }

    @Test
    void CheckGetDefaultHistory_isNotEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager); // Проверяем, что класс не null
    }

}