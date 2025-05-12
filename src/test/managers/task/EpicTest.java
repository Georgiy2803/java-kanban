package test.managers.task;

import main.managers.Managers;
import main.managers.task.TaskManager;
import main.model.Epic;
import main.model.Subtask;
import main.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EpicTest {

    TaskManager manager = Managers.getDefault();

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера.
    // Независимо от переданного id программа присваивает свой и задачи считаются разными
    @Test
    public void equals_returnFalse_passedIdsMatch() {
        Epic epic1 = manager.createEpic(new Epic("name", "description", 1));
        Epic epic2 = manager.createEpic(new Epic("name", "description", 1));
        assertFalse(epic1.equals(epic2));
    }

    @Test
    void createdEpicIsEqualReceived_byId() {
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что созданный Epic и полученный по id равны
        assertEquals(epic, manager.getTaskById(epic.getId()).get(), "Эпики не совпадают");
    }

    @Test
    void fieldsMatchList_whenCreatedEpic(){
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(epic.getName(), manager.getEpicById(epic.getId()).get().getName(), "Наименования Эпик не совпадают");
        assertEquals(epic.getDescription(), manager.getEpicById(epic.getId()).get().getDescription(), "Описание Эпик не совпадают");
    }

    @Test
    void deleteEpic_adnDeletedSubtasks() { //Проверка, что Эпик удаляется вместе с подзадачими
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = manager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        manager.createEpic(new Epic("Название Эпик3", "Описание Эпик3"));
        manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        assertEquals(3, manager.getEpics().size(), "В списке не верное количество Эпик");
        manager.deleteByIdEpic(epic1.getId());
        assertEquals(2, manager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(2, manager.getSubtask().size(), "В списке не верное количество подзадач");
        manager.deleteAllEpic();
        assertEquals(0, manager.getEpics().size(), "В списке не верное количество Эпик");
        assertEquals(0, manager.getSubtask().size(), "В списке не верное количество подзадач");

    }

}