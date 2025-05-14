package model;


import model.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @Test
    public void equals_returnTrue_passedIdsMatch() {
        Task task1 = new Task("name", "description", 1);
        Task task2 = new Task("name", "description", 1);
        assertTrue(task1.equals(task2));
    }
}
