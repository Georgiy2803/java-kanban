package managers.history;

import managers.Managers;
import managers.history.HistoryManager;
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

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager history;

    @BeforeEach
    public void init(){
        history = new InMemoryHistoryManager();
    }

    /* Переименовал тест, и оставил HISTORY_MAX_SIZE (puplic), потому что: если(когда) надо будет увеличить список
    до 20 или 100, не нужно будет делать новый тест, т.к. вся его логика в том, что он выдаёт чётко ограниченное
    количество историй, а это ограничение может легко измениться благодаря одной переменной с Вашей подачи. */
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

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void addTask_saveHistory_update() {
        Task task1 = new Task("Имя 1", "Описание 1"); // создаём задачу
        ArrayList<Task> tasklist = new ArrayList<>(); // создаём список имитируя добавление в InMemoryHistoryManager
        tasklist.add(task1); // добавили задачу в InMemoryHistoryManager
        history.add(task1); // добавляет запрос в историю просмотров
        Task updateTask1 = new Task("Имя 2", "Описание 2"); // // создаём обновлённую задачу
        tasklist.add(0,updateTask1); // обновляем задачу

        assertEquals(task1, history.getHistory().get(0), "Task не совпадают");
    }


    // данный тест воспринимаю как совершенно бессмысленный, т.к. InMemoryHistoryManager не самостоятельный класс на мой
    // взгляд сейчас, и чётко завязан InMemoryTaskManager и его логику. И проверять его методы выдумывая таки костыли
    // кажется бессмысленным.
    @Test
    void addTask_saveAllFields_addTask() {
        Task task1 = new Task("Имя 1", "Описание 1"); // создаём задачу
        history.add(task1); // просматриваем задачу

        // проверяем поля
        assertEquals("Имя 1", history.getHistory().get(0).getName());
        assertEquals("Описание 1", history.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, history.getHistory().get(0).getStatus());

    }
    // здесь я вообще не понимаю как нормально без логики InMemoryTaskManager(который создаёт id, привязывает id Subtask)
    // засунуть всё это в history, чтобы потом проверить, что я всё верно своими же руками добавил, а потом сравнить.
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

    /*
    Вообще кажется, что смысла тестов кроме метода add_deleteFirst_addedMoreThenTen() тут нет. Всё остальное надо
    тестировать через InMemoryTaskManager.
     Первоначальные тесты переместил в Тест InMemoryTaskManager, а тут мне кажется надо оставить только
     add_deleteFirst_addedMoreThenTen.
     */
}