package managers.model;


import model.Subtask;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtaskTest {



    @Test
    public void equals_returnTrue_passedIdsMatch() {
        Subtask subtask1 = new Subtask("name", "description", 1);
        Subtask subtask2 = new Subtask("name", "description", 1);
        assertTrue(subtask1.equals(subtask2));
    }


}
