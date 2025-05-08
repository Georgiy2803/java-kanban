
package test;

import org.junit.jupiter.api.Test;
import taskManager.Managers;
import taskManager.TaskManager;
import taskManager.model.Epic;
import taskManager.model.Status;
import taskManager.model.Subtask;
import taskManager.model.Task;
import static org.junit.jupiter.api.Assertions.*;



class InMemoryTaskManagerTest {
    TaskManager manager = Managers.getDefault();
    /*
    * Выполнено - проверьте, что экземпляры класса Task равны друг другу, если равен их id;
     * Выполнено - проверьте, что наследники класса Task равны друг другу, если равен их id;
    * Выполнено - проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
       - Такое не возможно уже на этапе создания объекта. Компилятор сразу выдаст ошибку при попытке добавить объект типа
         Эпик в подзадачу.
    * Выполнено - проверьте, что объект Subtask нельзя сделать своим же эпиком;
       - Такое не возможно уже на этапе создания объекта. Компилятор сразу выдаст ошибку при попытке добавить объект типа
         Subtask ввиде Эпик.
    * Выполнено - убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    * Выполнено - проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    * Выполнено - проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
       - Такое не возможно уже на этапе создания объекта. В конструктор id никогда передаётся.
    * Выполнено - создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
     */


    @Test
    void createTask() {
        Task task = manager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что созданный Task и полученный по id равны
        String message1 = "под.тест1 - Значения не совпадают (" + task + ", " + manager.getTaskById(task.getId()).get() + ")";
        assertEquals(task, manager.getTaskById(task.getId()).get(), message1);

        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        String message2 = "под.тест2 - Значения не совпадают (" + task.getName() + ", " + manager.getTaskById(task.getId()).get().getName() + ")";
        assertEquals(task.getName(), manager.getTaskById(task.getId()).get().getName(), message2);

        String message3 = "под.тест3 - Значения не совпадают (" + task.getName() + ", " + manager.getTaskById(task.getId()).get().getDescription() + ")";
        assertEquals(task.getDescription(), manager.getTaskById(task.getId()).get().getDescription(), message3);
    }

    @Test
    void createEpic() {
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что созданный Epic и полученный по id равны
        String message1 = "под.тест1 - Значения не совпадают (" + epic + ", " + manager.getEpicById(epic.getId()).get() + ")";
        assertEquals(epic, manager.getEpicById(epic.getId()).get(), message1);

        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        String message2 = "под.тест2 - Значения не совпадают (" + epic.getName() + ", " + manager.getEpicById(epic.getId()).get().getName() + ")";
        assertEquals(epic.getName(), manager.getEpicById(epic.getId()).get().getName(), message2);

        String message3 = "под.тест3 - Значения не совпадают (" + epic.getName() + ", " + manager.getEpicById(epic.getId()).get().getDescription() + ")";
        assertEquals(epic.getDescription(), manager.getEpicById(epic.getId()).get().getDescription(), message3);
    }

    @Test
    void createSubtask() {
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = manager.createSubtask(new Subtask("Название подзадачи", "Описание подзадачи", epic.getId()));
        // проверка, что созданный так и полученный по id равны
        String message1 = "под.тест1 - Значения не совпадают (" + subtask + ", " + manager.getSubtaskById(subtask.getId()).get() + ")";
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()).get(), message1);

        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        String message2 = "под.тест2 - Значения не совпадают (" + subtask.getName() + ", " + manager.getSubtaskById(subtask.getId()).get().getName() + ")";
        assertEquals(subtask.getName(), manager.getSubtaskById(subtask.getId()).get().getName(), message2);

        String message3 = "под.тест3 - Значения не совпадают (" + subtask.getName() + ", " + manager.getSubtaskById(subtask.getId()).get().getDescription() + ")";
        assertEquals(subtask.getDescription(), manager.getSubtaskById(subtask.getId()).get().getDescription(), message3);

        String message4 = "под.тест4 - Значения не совпадают (" + subtask.getId() + ", " + manager.getSubtaskById(subtask.getId()).get().getId() + ")";
        assertEquals(subtask.getId(), manager.getSubtaskById(subtask.getId()).get().getId(), message4);
    }

    @Test
    void deleteTask() {
        Task task1 = manager.createTask(new Task("Название задачи1", "Описание задачи1"));
        manager.createTask(new Task("Название задачи2", "Описание задачи2"));
        manager.createTask(new Task("Название задачи3", "Описание задачи3"));

        String message1 = "под.тест1 - Значения не совпадают (" + 3 + ", " + manager.getTasks().size() + ")";
        assertEquals(3, manager.getTasks().size(), message1);

        String message2 = "под.тест2 - Значения не совпадают (" + 2 + ", " + manager.getTasks().size() + ")";
        manager.deleteByIdTask(task1.getId());
        assertEquals(2, manager.getTasks().size(), message2);

        String message3 = "под.тест2 - Значения не совпадают (" + 0 + ", " + manager.getTasks().size() + ")";
        manager.deleteAllTasks();
        assertEquals(0, manager.getTasks().size(), message3);
    }

    @Test
    void deleteEpic() {
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = manager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        manager.createEpic(new Epic("Название Эпик3", "Описание Эпик3"));
        manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));


        String message1 = "под.тест1 - Значения не совпадают (" + 3 + ", " + manager.getEpics().size() + ")";
        assertEquals(3, manager.getEpics().size(), message1);

        manager.deleteByIdEpic(epic1.getId());
        String message2 = "под.тест2 - Значения не совпадают (" + 2 + ", " + manager.getEpics().size() + ")";
        assertEquals(2, manager.getEpics().size(), message2);


        String message3 = "под.тест3 - Значения не совпадают (" + 2 + ", " + manager.getSubtask().size() + ")";
        assertEquals(2, manager.getSubtask().size(), message3);


        manager.deleteAllEpic();
        String message4 = "под.тест5 - Значения не совпадают (" + 0 + ", " + manager.getEpics().size() + ")";
        String message5 = "под.тест4 - Значения не совпадают (" + 0 + ", " + manager.getSubtask().size() + ")";
        assertEquals(0, manager.getEpics().size(), message4);
        assertEquals(0, manager.getSubtask().size(), message5);

    }

    @Test
    void deleteSubtask() {
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = manager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        manager.createEpic(new Epic("Название Эпик3", "Описание Эпик3"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        // Проверяем, что у Эпик список подзадач равен 1
        String message1 = "под.тест1 - Значения не совпадают (" + 1 + ", " + manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size() + ")";
        assertEquals(1, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),message1);


        manager.deleteByIdSubtask(subtask1.getId()); // удаляем единственную подзадачу
        String message2 = "под.тест2 - Значения не совпадают (" + 2 + ", " + manager.getSubtask().size() + ")";
        assertEquals(2, manager.getSubtask().size(),message1);
        String message3 = "под.тест3 - Значения не совпадают (" + 2 + ", " + manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size() + ")";
        assertEquals(0, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),message2);

        manager.deleteAllSubtask(); // удаляем все подзадачи
        String message4 = "под.тест4 - Значения не совпадают (" + 0 + ", " + manager.getSubtask().size() + ")";
        assertEquals(0, manager.getSubtask().size(),message1);
        String message5 = "под.тест5 - Значения не совпадают (" + 2 + ", " + manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size() + ")";
        assertEquals(0, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),message2);

    }

    @Test
    void updateSubtask() {
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic1.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic1.getId()));


        String message1 = "под.тест1 - Значения не совпадают (" + Status.NEW + ", " + manager.getEpicById(epic1.getId()).get().getStatus() + ")";
        assertEquals(Status.NEW, manager.getEpicById(epic1.getId()).get().getStatus(),message1);

        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask3.getId(), Status.DONE));

        String message2 = "под.тест2 - Значения не совпадают (" + Status.IN_PROGRESS + ", " + manager.getEpicById(epic1.getId()).get().getStatus() + ")";
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic1.getId()).get().getStatus(),message2);

        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.DONE));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.DONE));

        String message3 = "под.тест3 - Значения не совпадают (" + Status.DONE + ", " + manager.getEpicById(epic1.getId()).get().getStatus() + ")";
        assertEquals(Status.DONE, manager.getEpicById(epic1.getId()).get().getStatus(),message3);

    }

}