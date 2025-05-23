package managers.task;

import managers.Managers;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {

    TaskManager taskManager;

    @BeforeEach
    public void init() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }


    // Тесты Task
    @Test
    void getTaskById_returnSameTaskObject() {
        Task task = taskManager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что созданный Task и полученный по id равны
        assertEquals(task, taskManager.getTaskById(task.getId()).get(), "Задачи не совпадают");
    }

    @Test
    void getTaskById_returnSameTaskcWithSAmeFields() {
        Task task = taskManager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(task.getName(), taskManager.getTaskById(task.getId()).get().getName(), "Наименования Task не совпадают");
        assertEquals(task.getDescription(), taskManager.getTaskById(task.getId()).get().getDescription(), "Описание Task не совпадают");
        assertEquals(task.getId(), taskManager.getTaskById(task.getId()).get().getId(), "id Task не совпадают");
        assertEquals(task.getStatus(), taskManager.getTaskById(task.getId()).get().getStatus(), "Статус Task не совпадают");
    }

    @Test
    void deleteTask_reflectedInList() {
        Task task1 = taskManager.createTask(new Task("Название задачи1", "Описание задачи1"));
        taskManager.createTask(new Task("Название задачи2", "Описание задачи2"));
        taskManager.createTask(new Task("Название задачи3", "Описание задачи3"));
        // проверка, что задачи удаляются в списке менеджера
        assertEquals(3, taskManager.getTasks().size(), "В списке не верное количество задач");
        taskManager.deleteByIdTask(task1.getId());
        assertEquals(2, taskManager.getTasks().size(), "В списке не верное количество задач");
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getTasks().size(), "В списке не верное количество задач");
    }


    // Тесты Epic

    @Test
    void getEpicById_returnSameEpicObject() { // get Epic By Id_end the same Epic object
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что созданный Epic и полученный по id равны
        assertEquals(epic, taskManager.getEpicById(epic.getId()).get(), "Эпики не совпадают");
    }

    @Test
    void getEpicById_returnSameEpicWithSAmeFields() {
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(epic.getName(), taskManager.getEpicById(epic.getId()).get().getName(), "Наименования Эпик не совпадают");
        assertEquals(epic.getDescription(), taskManager.getEpicById(epic.getId()).get().getDescription(), "Описание Эпик не совпадают");
        assertEquals(epic.getId(), taskManager.getEpicById(epic.getId()).get().getId(), "id Эпик не совпадают");
        assertEquals(epic.getStatus(), taskManager.getEpicById(epic.getId()).get().getStatus(), "Статус Эпик не совпадают");
        assertEquals(epic.getListSubtaskIds(), taskManager.getEpicById(epic.getId()).get().getListSubtaskIds(), "Списки подзадач Эпика не совпадают");
    }

    @Test
    void deleteEpic_deleteCorrespondingSubtasks() { //Проверка, что Эпик удаляется вместе с подзадачами
        Epic epic1 = taskManager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        taskManager.createEpic(new Epic("Название Эпик3", "Описание Эпик3"));
        taskManager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        taskManager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        taskManager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        assertEquals(3, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        taskManager.deleteByIdEpic(epic1.getId());
        assertEquals(2, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(2, taskManager.getSubtask().size(), "В списке не верное количество подзадач");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(0, taskManager.getSubtask().size(), "В списке не верное количество подзадач");
    }

    // Тесты Subtask

    @Test
    void getSubtaskById_returnSameSubtaskObject() {
        Epic epic = taskManager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Название подзазачи", "Описание подзазачи", epic.getId()));
        // проверка, что созданный Subtask и полученный по id равны
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()).get(), "Подзадачи не совпадают");
    }

    @Test
    void getSubtaskById_returnSameSubtaskWithSAmeFields() {
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
    void deleteSubtask_UpdatedListSubtaskEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = taskManager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        // Проверяем, что у Эпик список подзадач равен 1
        assertEquals(1, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик");

        taskManager.deleteByIdSubtask(subtask1.getId()); // удаляем единственную подзадачу
        assertEquals(2, taskManager.getSubtask().size(), "Не верное количество подзадач всего");
        assertEquals(0, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик1");

        taskManager.deleteAllSubtask(); // удаляем все подзадачи
        assertEquals(0, taskManager.getSubtask().size(), "Не верное количество подзадач всего");
        assertEquals(0, taskManager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик1");
        assertEquals(0, taskManager.getEpicById(epic2.getId()).get().getListSubtaskIds().size(), "Не верное количество подзадач у Эпик2");

    }

    @Test
    void updateSubtask_UpdateStatusEpic() {
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
    void getHistoryTask_returnOldUpdateTask() { // HistoryManager сохраняет предыдущую версию задачи и её данных
        Task task1 = taskManager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        taskManager.getTaskById(task1.getId()); // просматриваем задачу
        taskManager.updateTask(new Task("Имя 2", "Описание 2", task1.getId(), Status.DONE)); // обновляем задачу
        assertEquals(task1, taskManager.getHistory().get(0), "Task не совпадают");
    }

    @Test
    void getHistoryTask_returnSameTaskWithSAmeFields() {  // Полученная задача из истории соответствуют задаче в менеджере
        Task task1 = taskManager.createTask(new Task("Имя 1", "Описание 1")); // создаём задачу
        taskManager.getTaskById(task1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, taskManager.getHistory().get(0).getId());
        assertEquals("Имя 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }


    @Test
    void getHistoryEpic_returnSameEpicWithSAmeFields() {  // Полученный Эпик из истории соответствуют Эпик в менеджере
        Epic epic1 = taskManager.createEpic(new Epic("Имя 1", "Описание 1")); // создаём Эпик
        taskManager.getEpicById(epic1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(1, taskManager.getHistory().get(0).getId());
        assertEquals("Имя 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }

    @Test
    void getHistorySubtask_returnSameSubtaskWithSAmeFields() {  // Полученный Subtask из истории соответствуют Subtask в менеджере
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпик 1")); // создаём Эпик
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId())); // создаём задачу
        taskManager.getSubtaskById(subtask1.getId()); // просматриваем задачу
        // проверяем поля
        assertEquals(2, taskManager.getHistory().get(0).getId());
        assertEquals("Подзадача 1", taskManager.getHistory().get(0).getName());
        assertEquals("Описание подзадачи 1", taskManager.getHistory().get(0).getDescription());
        assertEquals(Status.NEW, taskManager.getHistory().get(0).getStatus());
    }
}
