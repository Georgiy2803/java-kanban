package managers.task;

import exception.IntersectionException;
import exception.NotFoundException;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InMemoryTaskManagerTest {

    TaskManager taskManager;


    @BeforeEach
    public void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }


    // Тесты Task
    @Test
    void getTaskById_returnSameTaskObject() throws NotFoundException, IntersectionException {
        Task task = taskManager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что созданный Task и полученный по id равны
        assertEquals(task, taskManager.getTaskById(task.getId()).get(), "Задачи не совпадают");
    }

    @Test
    void getTaskById_returnSameTaskcWithSAmeFields() throws NotFoundException, IntersectionException {
        Task task = taskManager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(task.getName(), taskManager.getTaskById(task.getId()).get().getName(), "Наименования Task не совпадают");
        assertEquals(task.getDescription(), taskManager.getTaskById(task.getId()).get().getDescription(), "Описание Task не совпадают");
        assertEquals(task.getId(), taskManager.getTaskById(task.getId()).get().getId(), "id Task не совпадают");
        assertEquals(task.getStatus(), taskManager.getTaskById(task.getId()).get().getStatus(), "Статус Task не совпадают");
    }

    @Test
    void deleteTask_reflectedInList() throws IntersectionException {
        Task task1 = taskManager.createTask(new Task("Название задачи1", "Описание задачи1"));
        taskManager.createTask(new Task("Название задачи2", "Описание задачи2"));
        taskManager.createTask(new Task("Название задачи3", "Описание задачи3"));
        // проверка, что задачи удаляются в списке менеджера
        assertEquals(3, taskManager.getTasks().size(), "В списке не верное количество задач");
        taskManager.deleteTaskById(task1.getId());
        assertEquals(2, taskManager.getTasks().size(), "В списке не верное количество задач");
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(), "В списке не верное количество задач");
    }

    // Тесты Epic

    @Test
    void getEpicById_returnSameEpicObject() throws NotFoundException { // get Epic By Id_end the same Epic object
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что созданный Epic и полученный по id равны
        assertEquals(epic, taskManager.getEpicById(epic.getId()).get(), "Эпики не совпадают");
    }

    @Test
    void getEpicById_returnSameEpicWithSAmeFields() throws NotFoundException {
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(epic.getName(), taskManager.getEpicById(epic.getId()).get().getName(), "Наименования Эпик не совпадают");
        assertEquals(epic.getDescription(), taskManager.getEpicById(epic.getId()).get().getDescription(), "Описание Эпик не совпадают");
        assertEquals(epic.getId(), taskManager.getEpicById(epic.getId()).get().getId(), "id Эпик не совпадают");
        assertEquals(epic.getStatus(), taskManager.getEpicById(epic.getId()).get().getStatus(), "Статус Эпик не совпадают");
        assertEquals(epic.getListSubtaskIds(), taskManager.getEpicById(epic.getId()).get().getListSubtaskIds(), "Списки подзадач Эпика не совпадают");
    }

    @Test
    void deleteEpic_deleteCorrespondingSubtasks() throws IntersectionException { //Проверка, что Эпик удаляется вместе с подзадачами
        Epic epic1 = taskManager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        taskManager.createEpic(new Epic("Название Эпик3", "Описание Эпик3"));
        taskManager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        taskManager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        taskManager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        assertEquals(3, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        taskManager.deleteEpicById(epic1.getId());
        assertEquals(2, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(2, taskManager.getSubtask().size(), "В списке не верное количество подзадач");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(0, taskManager.getSubtask().size(), "В списке не верное количество подзадач");
    }

    // Тесты Subtask

    @Test
    void getSubtaskById_returnSameSubtaskObject() throws NotFoundException, IntersectionException {
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Название подзазачи", "Описание подзазачи", epic.getId()));
        // проверка, что созданный Subtask и полученный по id равны
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()).get(), "Подзадачи не совпадают");
    }

    @Test
    void getSubtaskById_returnSameSubtaskWithSAmeFields() throws NotFoundException, IntersectionException {
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Название подзазачи", "Описание подзазачи", epic.getId()));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(subtask.getName(), taskManager.getSubtaskById(subtask.getId()).get().getName(), "Наименования подзазачи не совпадают");
        assertEquals(subtask.getDescription(), taskManager.getSubtaskById(subtask.getId()).get().getDescription(), "Описание подзазачи не совпадают");
        assertEquals(subtask.getId(), taskManager.getSubtaskById(subtask.getId()).get().getId(), "id подзазачи не совпадают");
        assertEquals(subtask.getStatus(), taskManager.getSubtaskById(subtask.getId()).get().getStatus(), "Статус подзазачи не совпадают");
        assertEquals(subtask.getEpicId(), taskManager.getSubtaskById(subtask.getId()).get().getEpicId(), "id связанного Эпик не совпадают");
    }

    @Test
    void deleteSubtask_UpdatedListSubtaskEpic() throws NotFoundException, IntersectionException {
        Epic epic1 = taskManager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        // Проверяем, что у Эпик список подзадач равен 1
        assertEquals(1, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик");

        taskManager.deleteSubtaskById(subtask1.getId()); // удаляем единственную подзадачу
        assertEquals(2, taskManager.getSubtask().size(), "Не верное количество подзадач всего");
        assertEquals(0, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик1");

        taskManager.deleteAllSubtask(); // удаляем все подзадачи
        assertEquals(0, taskManager.getSubtask().size(), "Не верное количество подзадач всего");
        assertEquals(0, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик1");
        assertEquals(0, taskManager.getEpicById(epic2.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик2");

    }

    @Test
    void updateSubtask_UpdateStatusEpic() throws IntersectionException, NotFoundException {
        Epic epic1 = taskManager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic1.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic1.getId()));

        assertEquals(Status.NEW, taskManager.getEpicById(epic1.getId()).get().getStatus(), "Статус Эпик не верный");

        taskManager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask3.getId(), Status.DONE));
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic1.getId()).get().getStatus(), "Статус Эпик не верный");

        taskManager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.DONE));
        taskManager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.DONE));
        assertEquals(Status.DONE, taskManager.getEpicById(epic1.getId()).get().getStatus(), "Статус Эпик не верный");
    }

    // тесты getHistory

    @Test
    void getHistoryTask_returnOldUpdateTask() throws IntersectionException, NotFoundException { // HistoryManager сохраняет предыдущую версию задачи и её данных
        Task task1 = taskManager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        taskManager.getTaskById(task1.getId()); // просматриваем задачу
        taskManager.updateTask(new Task("Имя 2", "Описание 2", task1.getId(), Status.DONE)); // обновляем задачу
        assertEquals(task1, taskManager.getHistory().get(0), "Task не совпадают");
    }

    @Test
    void getHistoryTask_returnSameTaskWithSAmeFields() throws NotFoundException, IntersectionException {  // Полученная задача из истории соответствуют задаче в менеджере
        Task task1 = taskManager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        taskManager.getTaskById(task1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, taskManager.getHistory().get(0).getId());
        assertEquals("Имя 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }


    @Test
    void getHistoryEpic_returnSameEpicWithSAmeFields() throws NotFoundException {  // Полученный Эпик из истории соответствуют Эпик в менеджере
        Epic epic1 = taskManager.createEpic(new Epic("Имя 1", "Описание 1")); // создаём Эпик
        taskManager.getEpicById(epic1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, taskManager.getHistory().get(0).getId());
        assertEquals("Имя 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }

    @Test
    void getHistorySubtask_returnSameSubtaskWithSAmeFields() throws NotFoundException, IntersectionException {  // Полученный Subtask из истории соответствуют Subtask в менеджере
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпик 1")); // создаём Эпик
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId())); // создаём задачу
        taskManager.getSubtaskById(subtask1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(2, taskManager.getHistory().get(0).getId());
        assertEquals("Подзадача 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание подзадачи 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }

    @Test
    void remove_deleteAllTasks_removeAllTasksInHistory () throws NotFoundException, IntersectionException {
        taskManager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        taskManager.createTask(new Task("Имя 2", "Описание 2")); // создаём задачу
        taskManager.createTask(new Task("Имя 3", "Описание 3")); // создаём задачу
        taskManager.getTaskById(1); // добавляет запрос в историю просмотров
        taskManager.getTaskById(2); // добавляет запрос в историю просмотров
        taskManager.getTaskById(3); // добавляет запрос в историю просмотров

        assertEquals(3, taskManager.getHistory().size(), "Размер Истории просмотров не соответствует");
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getHistory().size(), "Истории просмотров не пустая");
    }

    @Test
    void remove_deleteAllEpic_removeAllEpicAndSubtaskInHistory () throws NotFoundException, IntersectionException {
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание Эпик2"));
        Epic epic3 = taskManager.createEpic(new Epic("Эпик 3", "Описание Эпик2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "для Эпик 1", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", " для Эпик 2", epic2.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", " для Эпик 3", epic3.getId()));
        taskManager.getEpicById(1); // добавляет запрос в историю просмотров
        taskManager.getEpicById(2); // добавляет запрос в историю просмотров
        taskManager.getEpicById(3); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(4); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(5); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(6); // добавляет запрос в историю просмотров

        assertEquals(6, taskManager.getHistory().size(), "Размер Истории просмотров не соответствует");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getHistory().size(), "Истории просмотров не пустая");
    }

    @Test
    void remove_deleteAllSubtask_removeAllSubtaskInHistory () throws NotFoundException, IntersectionException {
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание Эпик2"));
        Epic epic3 = taskManager.createEpic(new Epic("Эпик 3", "Описание Эпик2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "для Эпик 1", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", " для Эпик 2", epic2.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", " для Эпик 3", epic3.getId()));
        taskManager.getEpicById(1); // добавляет запрос в историю просмотров
        taskManager.getEpicById(2); // добавляет запрос в историю просмотров
        taskManager.getEpicById(3); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(4); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(5); // добавляет запрос в историю просмотров
        taskManager.getSubtaskById(6); // добавляет запрос в историю просмотров

        assertEquals(6, taskManager.getHistory().size(), "Размер Истории просмотров не соответствует");
        taskManager.deleteAllSubtask();
        assertEquals(3, taskManager.getHistory().size(), "Истории просмотров не пустая");
    }


    /* Добавление задач и проверка, что они все попали в списки (HashMap) и отсортированный список (sortedTasks).
    Так же проверка, что задачи отсортированные в списке верно.
     */
    @Test
    void addTasks_checkSizeOfList_checkSortedList () throws IntersectionException {
        // создаём задачи
        taskManager.createTask(new Task("Task1", "", LocalDateTime.of(2025, 6, 21, 11, 1), Duration.ofMinutes(58)));
        taskManager.createTask(new Task("Task2", "", LocalDateTime.of(2025, 6, 21, 13, 0), Duration.ofMinutes(20)));
        taskManager.createTask(new Task("Task3", "", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(20)));
        taskManager.createTask(new Task("Task4", "", LocalDateTime.of(2025, 6, 21, 12, 21), Duration.ofMinutes(38)));
        taskManager.createTask(new Task("Task5", "", LocalDateTime.of(2025, 6, 21, 10, 0), Duration.ofMinutes(60)));
        taskManager.createTask(new Task("Task6", "Описание 1")); // создаём задачу без времени
        taskManager.createTask(new Task("Task7", "Описание 2")); // создаём задачу без времени

        taskManager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        taskManager.createEpic(new Epic("Эпик 2", "1 подзадача"));
        taskManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 15, 1), Duration.ofMinutes(55)));
        taskManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 16, 6), Duration.ofMinutes(20)));
        taskManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 15, 57), Duration.ofMinutes(8)));
        taskManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 9));

        // проверка количества Task в списке (HashMap)
        assertEquals(7, taskManager.getTasks().size(), "В списке хеш-таблицы не верное количество Task");
        // количество Эпиков в списке (HashMap)
        assertEquals(2, taskManager.getEpics().size(), "В списке хеш-таблицы не верное количество Эпиков");
        // количество Subtask в списке (HashMap)
        assertEquals(4, taskManager.getSubtask().size(), "В списке хеш-таблицы не верное количество Subtask");

        // количество задач в отсортированном списке по приоритету (startTime)
        assertEquals(9, taskManager.getPrioritizedTasks().size(), "В списке отсортирванные задачи по приоритету (startTime) не верное количество задач");



        // Проверка правильности сортировки задач
        assertEquals("Task5", taskManager.getPrioritizedTasks().get(0).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Task1", taskManager.getPrioritizedTasks().get(1).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Task3", taskManager.getPrioritizedTasks().get(2).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Task4", taskManager.getPrioritizedTasks().get(3).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Task2", taskManager.getPrioritizedTasks().get(4).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Эпик 1", taskManager.getPrioritizedTasks().get(5).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Подзадача 1", taskManager.getPrioritizedTasks().get(6).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Подзадача 3", taskManager.getPrioritizedTasks().get(7).getName(), "Задача в отсортированном списке не правильно расположена");
        assertEquals("Подзадача 2", taskManager.getPrioritizedTasks().get(8).getName(), "Задача в отсортированном списке не правильно расположена");

    }

    @Test
    void addTasks_checkIntersectionOfTasks() throws Exception {
        // Этап 1: Положительное тестирование (добавляем задачи без пересечения)
        taskManager.createTask(new Task("Task1", "", LocalDateTime.of(2025, 6, 21, 11, 1), Duration.ofMinutes(58)));
        taskManager.createTask(new Task("Task2", "", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(20)));
        taskManager.createTask(new Task("Task4", "", LocalDateTime.of(2025, 6, 21, 12, 21),Duration.ofMinutes(38)));
        taskManager.createTask(new Task("Task5", "", LocalDateTime.of(2025, 6, 21, 10, 0), Duration.ofMinutes(60)));

        // Этап 2: Негативное тестирование (пытаемся добавить задачу с пересечением)
        try {
            taskManager.createTask(new Task("Task3", "",LocalDateTime.of(2025, 6, 21, 12, 0),Duration.ofMinutes(20))); // Эта задача пересекается с Task2
            fail("Ожидается исключение IntersectionException, но оно не было возбуждено");
        } catch (IntersectionException expected) {}

        // Этап 3: Итоговая проверка количества задач
        // Проверяем, что в HashMap количество Task меньше на одну, т.к. одна задача (Task3) не была добавлена из-за пересечения
        assertEquals(4, taskManager.getTasks().size(),
                "Количество задач в хеш-таблице должно быть равно 4, так как одна задача не была добавлена из-за пересечения");
    }
}
