package test.managers.task;

import main.managers.Managers;
import main.managers.task.TaskManager;
import main.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*

*  - проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;

Комет раний рьвьюера.
у класса Task есть публичный конструктор который принимает в себя id и сеттер который проставляет id
следовательно такое возможно
Task task = newTask("name", "description", 1, Status.NEW);
надо проверить что если в тасковом менеджере уже есть таск с id 1 то он не перепишется, а новый таск добавится и идентифктаор у него заменится

Я понимаю о чём пишете Вы, но не понимаю другого. Конструкт о который вы упомянули, нужен для обновления Task. Id в нём
используется для, того чтобы по нему найти обновляемую задачу. Все поля (кроме id) при обновлении можно поменять, соответственно
в Мапе под этим id будет уже другая задача (обновлённая). Из-за этого я не понимаю, какой нужно сделать тест? Что обновлённая
задача не равна ранее созданной? Типо обновление прошло удачно?

 */

public class TaskTest {

    TaskManager manager = Managers.getDefault();

    @Test
    void createdTaskIsEqualReceived_byId() {
        Task task = manager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что созданный Task и полученный по id равны
        assertEquals(task, manager.getTaskById(task.getId()).get(), "Задачи не совпадают");
    }

    @Test
    void fieldsMatchList_whenCreatedTask(){
        Task task = manager.createTask(new Task("Название задачи", "Описание задачи"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(task.getName(), manager.getTaskById(task.getId()).get().getName(), "Наименования задачи не совпадают");
        assertEquals(task.getDescription(), manager.getTaskById(task.getId()).get().getDescription(), "Описание задачи не совпадают");
    }

    @Test
    void deleteTask_reflectedInList() {
        Task task1 = manager.createTask(new Task("Название задачи1", "Описание задачи1"));
        manager.createTask(new Task("Название задачи2", "Описание задачи2"));
        manager.createTask(new Task("Название задачи3", "Описание задачи3"));
        // проверка, что задачи удаляются в списке менеджера
        assertEquals(3, manager.getTasks().size(), "В списке не верное количество задач");
        manager.deleteByIdTask(task1.getId());
        assertEquals(2, manager.getTasks().size(), "В списке не верное количество задач");
        manager.deleteAllTasks();
        assertEquals(0, manager.getTasks().size(), "В списке не верное количество задач");
    }

}
