package managers.history;

import managers.Managers;
import managers.history.HistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

import static managers.history.InMemoryHistoryManager.HISTORY_MAX_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager history;

    @BeforeEach
    public void init(){
        history = new InMemoryHistoryManager();
    }


    @Test
    void add_deleteFirst_addedMoreThen_MAX_SIZE() { // отражение последних задач при просмотре
        int addSize = 15; // количество добавляемых задач для итерации
        for (int i = 1; i <= addSize; i++) {
            history.add(new Task("Название задачи: " + i, "Описание задачи: " + i));
        }
        // Проверяем, что размер истории соответствует ожидаемому
        assertEquals(HISTORY_MAX_SIZE, history.getHistory().size(),
                "Не совпадает ограничение и размер таблицы");
        // Проверяем, что последняя просмотренная задача, так же последняя в списке истории просмотров
        assertEquals("Название задачи: " + addSize, history.getHistory().get(HISTORY_MAX_SIZE - 1).getName(),
                "Не совпадают последние задачи");
    }

    @Test
    void add_saveHistoryIsNotEqual_UpdateTask() { // добавляемые задачи в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        Task task = new Task("Имя 1", "Описание 1"); // создаём задачу
        history.add(task); // добавляет запрос в историю просмотров
        //  обновляем поля задачи
        task.setName("Имя 2");
        task.setDescription("Описание 2");
        task.setId(2);
        task.setStatus(Status.IN_PROGRESS);

        assertNotEquals(task, history.getHistory().get(0), "Task одинаковые");
        assertNotEquals(task.getName(), history.getHistory().get(0).getName());
        assertNotEquals(task.getDescription(), history.getHistory().get(0).getDescription());
        assertNotEquals(task.getId(), history.getHistory().get(0).getId());
        assertNotEquals(task.getStatus(), history.getHistory().get(0).getStatus());
    }

    @Test
    void add_saveHistoryIsNotEqual_UpdateEpic() {
        Epic epic = new Epic("Имя 1", "Описание 1"); // создаём задачу
        history.add(epic); // добавляет запрос в историю просмотров
        //  обновляем поля задачи
        epic.setName("Имя 2");
        epic.setDescription("Описание 2");
        epic.setId(2);
        epic.setStatus(Status.IN_PROGRESS);

        assertNotEquals(epic, history.getHistory().get(0), "Epic одинаковые");
        assertNotEquals(epic.getName(), history.getHistory().get(0).getName());
        assertNotEquals(epic.getDescription(), history.getHistory().get(0).getDescription());
        assertNotEquals(epic.getId(), history.getHistory().get(0).getId());
        assertNotEquals(epic.getStatus(), history.getHistory().get(0).getStatus());
    }

    @Test
    void add_saveHistoryIsNotEqual_UpdateSubtask() {
        Subtask subtask = new Subtask("Имя 1", "Описание 1",1); // создаём задачу
        history.add(subtask); // добавляет запрос в историю просмотров
        //  обновляем поля задачи
        subtask.setName("Имя 2");
        subtask.setDescription("Описание 2");
        subtask.setId(2);
        subtask.setStatus(Status.IN_PROGRESS);

        assertNotEquals(subtask, history.getHistory().get(0), "Subtask одинаковые");
        assertNotEquals(subtask.getName(), history.getHistory().get(0).getName());
        assertNotEquals(subtask.getDescription(), history.getHistory().get(0).getDescription());
        assertNotEquals(subtask.getId(), history.getHistory().get(0).getId());
        assertNotEquals(subtask.getStatus(), history.getHistory().get(0).getStatus());
    }

    @Test
    void add_saveAllFields_addTask() {
        Task task1 = new Task("Имя 1", "Описание 1"); // создаём задачу
        history.add(task1); // просматриваем задачу

        // проверяем поля
        assertEquals("Имя 1", history.getHistory().get(0).getName());
        assertEquals("Описание 1", history.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, history.getHistory().get(0).getStatus());
    }

    @Test
    void add_saveAllFields_addEpic() {
        Epic epic1 = new Epic("Имя 1", "Описание 1"); // создаём Эпик
        history.add(epic1); // просматриваем Эпик

        // проверяем поля
        assertEquals("Имя 1", history.getHistory().get(0).getName());
        assertEquals("Описание 1", history.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, history.getHistory().get(0).getStatus());
    }
    // точно также как у Эпик
    @Test
    void add_saveAllFields_addedSubtask() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпик 1"); // создаём Эпик
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId()); // создаём подзадачу
        history.add(subtask1); // просматриваем подзадачу

        // проверяем поля
        assertEquals("Подзадача 1", history.getHistory().get(0).getName());
        assertEquals("Описание подзадачи 1", history.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, history.getHistory().get(0).getStatus());
    }

}