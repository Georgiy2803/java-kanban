
package test;

import org.junit.jupiter.api.Test;
import taskManager.*;
import taskManager.model.Epic;
import taskManager.model.Subtask;
import taskManager.model.Task;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager manager = Managers.getDefault();

    @Test
    void checkGetHistory() { // проверка работы HistoryManager
        // создаём 5 Task
        for (int i = 1; i <= 5; i++) {
            manager.createTask(new Task("Название задачи: " + i, "Описание задачи: " + i));
        }
        // создаём 5 Epic
        for (int i = 1; i <= 5; i++) {
            manager.createEpic(new Epic("Название Эпик: " + i, "Описание Эпик: " + i));
        }
        // создаём 5 Subtask
        for (int i = 1; i <= 5; i++) {
            // Данная строчка "manager.getEpics().get(i-1).getId()" вытаскивает id Эпик из Мапа куда они добавились
            manager.createSubtask(new Subtask("Название подзадачи: " + i, "Описание подзадачи: " + i, manager.getEpics().get(i - 1).getId()));
        }

        for (int i = 1; i <= 15; i++) { // 15 раз произведет просмотр задач в хаотичном порядке
            Random random = new Random();
            int get = 1+random.nextInt(3); // хаотично выбирает какой сделать запрос по типу задачи (Task, Epic, Subtask)
            int id = 1+random.nextInt(5); // хаотично выбирает id

            switch (get) { // производим просмотр задач случайным образом
                case 1: // условие
                    manager.getTaskById(id);
                    break;
                case 2: // условие
                    manager.getEpicById(id + 5);
                    break;
                case 3: // условие
                    manager.getSubtaskById(id + 10);
                    break;
            }
        }

        String message1 = "под.тест1 - Значения не совпадают (" + 10 + ", " + manager.getHistory().size() + ")";
        assertEquals(10, manager.getHistory().size(), message1);
    }
}
