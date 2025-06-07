package managers.history;

import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class InMemoryHistoryManagerTest {
    InMemoryHistoryManager history;

    @BeforeEach
    public void init() {
        history = new InMemoryHistoryManager();
    }

    @Test
    void add_addReplay_movLastList() { // проверяем, что при повторном добавлении существующей задачи она перемещается в конец списка
        Task task1 = new Task("Имя 1", "Описание 1",1); // создаём задачу
        Task task2 = new Task("Имя 2", "Описание 2",2); // создаём задачу
        Task task3 = new Task("Имя 1", "Описание 1",1); // создаём задачу
        history.add(task1); // добавляет запрос в историю просмотров
        history.add(task2); // добавляет запрос в историю просмотров
        history.add(task3); // добавляет запрос в историю просмотров
        // проверяем, что при повторном добавлении существующей задачи она перемещается в конец списка.
        assertEquals(task1, history.getHistory().get(1), "Task не одинаковые");
    }


    /*
    Тесты для удаления первого, последнего и среднего элементов, удаления несуществующего идентификатора, удаления из
     пустой истории и последовательного удаления всех элементов для проверки состояния истории.
     */
    @Test
    void remove_firstLastMiddleAndAllElements() {
        Task task1 = new Task("Имя 1", "Описание 1",1); // создаём задачу
        Task task2 = new Task("Имя 2", "Описание 2",2); // создаём задачу
        Task task3 = new Task("Имя 3", "Описание 3",3); // создаём задачу
        Task task4 = new Task("Имя 4", "Описание 4",4); // создаём задачу
        history.add(task1); // добавляет запрос в историю просмотров
        history.add(task2); // добавляет запрос в историю просмотров
        history.add(task3); // добавляет запрос в историю просмотров
        history.add(task4); // добавляет запрос в историю просмотров


        history.remove(2); // проверка удаления среднего элемента
        assertEquals(3, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(1); // проверка удаления первого элемента
        assertEquals(2, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(4); // проверка удаления последнего элемента
        assertEquals(1, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(3); // проверка удаления единственного элемента
        assertEquals(0, history.getHistory().size(), "размер Истории не соответствует ожидаемому");

        history.remove(2); // проверка удаления несуществующего идентификатора
        assertEquals(0, history.getHistory().size(), "размер Истории не соответствует ожидаемому");
    }

    @Test
    void getHistory_whenEmpty_returnsEmptyList() {
        List<Task> historyList = history.getHistory();
        assertEquals(0, historyList.size(), "История должна быть пустой, если она пуста или все задачи удалены");
    }

    @Test
        // add_verifySizeAndLastTask - вариант названия теста от Яндекс GPT
    void add_comparNumberOfTaskInList() { // в списке столько же задач, сколько мы в него добавили
        int addNumberOfTask = 30; // количество добавляемых задач для итерации
        for (int i = 1; i <= addNumberOfTask; i++) {
            history.add(new Task("Название задачи: " + i, "Описание задачи: " + i, i));
        }
        // Проверяем, что размер истории соответствует ожидаемому
        assertEquals(addNumberOfTask, history.getHistory().size(),
                "Не совпадает ограничение и размер таблицы");
        // Проверяем, что последняя просмотренная задача, так же последняя в списке истории просмотров
        assertEquals("Название задачи: " + addNumberOfTask, history.getHistory().get(addNumberOfTask - 1).getName(),
                "Не совпадают последние задачи");
    }

    @Test
      // addAndRemove_verifySize - вариант от Яндекс GPT
    void delete_comparNumberOfTaskInList() { // в списке столько же задач, сколько мы в него добавили, а потом удалили
        int addNumberOfTask = 30; // количество добавляемых задач для итерации
        for (int i = 1; i <= addNumberOfTask; i++) {
            history.add(new Task("Название задачи: " + i, "Описание задачи: " + i, i));
        }
        int deleteNumberOfTask = 10;
        for (int i = 1; i <= deleteNumberOfTask; i++) {
            history.remove(i);
        }
        // Проверяем, что размер истории соответствует ожидаемому
        assertEquals(addNumberOfTask - deleteNumberOfTask, history.getHistory().size(),
                "Не совпадает ограничение и размер таблицы");
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
    void add_saveHistoryIsNotEqual_UpdateSubtask() { // при изменении задачи, она должна оставаться неизменной в Истории просмотров
        Subtask subtask = new Subtask("Имя 1", "Описание 1", 1); // создаём задачу
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
