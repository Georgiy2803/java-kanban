package model;


import model.Epic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EpicTest {

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера.
    @Test
    public void equals_returnTrue_passedIdsMatch() {
        Epic epic1 = new Epic("name", "description", 1);
        Epic epic2 = new Epic("name", "description", 1);
        assertTrue(epic1.equals(epic2));
    }

    @Test
    // addSubtaskId_selfId_notAdded - вариант от Яндекс GPT
    public void add_IdSubtaskDontAdd_sameIdAsEpic() { // Нельзя добавить id Эпика к себе в подзадачи
        Epic epic = new Epic("name", "description", 1);
        epic.addSubtaskId(1); // пытаемся добавить свой id к себе в подзадачи
        assertTrue(epic.getListSubtaskIds().isEmpty()); // убеждаемся, что этого не произошло
    }

}