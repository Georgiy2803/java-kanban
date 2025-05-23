package managers.history;

import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Epic;
import model.Subtask;
import model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class InMemoryHistoryManagerTest {
    InMemoryHistoryManager history;

    @BeforeEach
    public void init() {
        history = new InMemoryHistoryManager();
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
        System.out.println(history.getHistory().get(0));
        //  обновляем поля задачи
        task.setName("Имя 2");
        task.setDescription("Описание 2");
        task.setId(2);
        task.setStatus(Status.IN_PROGRESS);
        System.out.println(history.getHistory().get(0));

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

        System.out.println(subtask + " , " + history.getHistory().get(0));
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
