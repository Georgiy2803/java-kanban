package test.managers.task;

import main.managers.Managers;
import main.managers.task.TaskManager;
import main.model.Epic;
import main.model.Status;
import main.model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    TaskManager manager = Managers.getDefault();

    @Test
    void createdSubtaskIsEqualReceived_byId() {
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = manager.createSubtask(new Subtask("Название подзазачи", "Описание подзазачи", epic.getId() ));
        // проверка, что созданный Subtask и полученный по id равны
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()).get(), "Подзадачи не совпадают");
    }

    @Test
    void fieldsMatchList_whenCreatedSubtask(){
        Epic epic = manager.createEpic(new Epic("Название Эпик", "Описание Эпик"));
        Subtask subtask = manager.createSubtask(new Subtask("Название подзазачи", "Описание подзазачи", epic.getId() ));
        // проверка, что переданные поля при создании метода, совпадают с теми, что есть в менеджере
        assertEquals(subtask.getName(), manager.getSubtaskById(subtask.getId()).get().getName(), "Наименования Эпик не совпадают");
        assertEquals(subtask.getDescription(), manager.getSubtaskById(subtask.getId()).get().getDescription(), "Описание Эпик не совпадают");
    }

    @Test
    void deleteSubtask_andUpdatedListSubtaskEpic() {
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Epic epic2 = manager.createEpic(new Epic("Название Эпик2", "Описание Эпик2"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic2.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic2.getId()));

        // Проверяем, что у Эпик список подзадач равен 1
        assertEquals(1, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),"Не верное количество подзадач у Эпик");

        manager.deleteByIdSubtask(subtask1.getId()); // удаляем единственную подзадачу
        assertEquals(2, manager.getSubtask().size(),"Не верное количество подзадач всего");
        assertEquals(0, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),"Не верное количество подзадач у Эпик1");

        manager.deleteAllSubtask(); // удаляем все подзадачи
        assertEquals(0, manager.getSubtask().size(),"Не верное количество подзадач всего");
        assertEquals(0, manager.getEpicById(epic1.getId()).get().getListSubtaskIds().size(),"Не верное количество подзадач у Эпик1");
        assertEquals(0, manager.getEpicById(epic2.getId()).get().getListSubtaskIds().size(),"Не верное количество подзадач у Эпик2");

    }

    @Test
    void updateSubtask_andUpdateSubtask() {
        Epic epic1 = manager.createEpic(new Epic("Название Эпик1", "Описание Эпик1"));
        Subtask subtask1 = manager.createSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(new Subtask("Название подзадачи2", "Описание подзадачи2", epic1.getId()));
        Subtask subtask3 = manager.createSubtask(new Subtask("Название подзадачи3", "Описание подзадачи1", epic1.getId()));

        assertEquals(Status.NEW, manager.getEpicById(epic1.getId()).get().getStatus(),"Статус Эпик не верный");

        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask3.getId(), Status.DONE));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic1.getId()).get().getStatus(),"Статус Эпик не верный");

        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask1.getId(), Status.DONE));
        manager.updateSubtask(new Subtask("Название подзадачи1", "Описание подзадачи3", subtask2.getId(), Status.DONE));
        assertEquals(Status.DONE, manager.getEpicById(epic1.getId()).get().getStatus(),"Статус Эпик не верный");

    }
}
