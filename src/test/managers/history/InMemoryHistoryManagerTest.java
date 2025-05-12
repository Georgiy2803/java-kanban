package test.managers.history;

import main.managers.Managers;
import main.managers.history.HistoryManager;
import main.managers.task.TaskManager;
import main.model.Status;
import org.junit.jupiter.api.Test;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;

import static main.managers.history.InMemoryHistoryManager.HISTORY_MAX_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefault();
    HistoryManager history = Managers.getDefaultHistory();

    @Test
    void add_deleteFirst_addedMoreThenTen() { // отражение последних задач при просмотре
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

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void outputOldTask_WhenUpdating() {
        Task task1 = manager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        manager.getTaskById(task1.getId()); // просматриваем задачу
        manager.updateTask(new Task("Имя 2", "Описание 2", task1.getId(), Status.DONE)); // обновляем задачу
        assertEquals(task1, history.getHistory().get(0), "Task не совпадают");
    }


    @Test
    void add_saveAllFields_addedTask() {
        Task task1 = manager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        manager.getTaskById(task1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, manager.getTaskById(task1.getId()).get().getId());
        assertEquals("Имя 1", manager.getTaskById(task1.getId()).get().getName());
        assertEquals("Описание 1", manager.getTaskById(task1.getId()).get().getDescription());
        assertEquals(Status.NEW, manager.getTaskById(task1.getId()).get().getStatus());

    }

    @Test
    void add_saveAllFields_addedEpic() {
        Epic epic1 = manager.createEpic(new Epic("Имя 1", "Описание 1")); // создаём Эпик
        manager.getEpicById(epic1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, manager.getEpicById(epic1.getId()).get().getId());
        assertEquals("Имя 1", manager.getEpicById(epic1.getId()).get().getName());
        assertEquals("Описание 1", manager.getEpicById(epic1.getId()).get().getDescription());
        assertEquals(Status.NEW, manager.getEpicById(epic1.getId()).get().getStatus());

    }

    @Test
    void add_saveAllFields_addedSubtask() {
        Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Описание эпик 1")); // создаём Эпик
        Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId())); // создаём задачу
        manager.getSubtaskById(subtask1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(2, manager.getSubtaskById(subtask1.getId()).get().getId());
        assertEquals("Подзадача 1", manager.getSubtaskById(subtask1.getId()).get().getName());
        assertEquals("Описание подзадачи 1", manager.getSubtaskById(subtask1.getId()).get().getDescription());
        assertEquals(Status.NEW, manager.getSubtaskById(subtask1.getId()).get().getStatus());

    }


}