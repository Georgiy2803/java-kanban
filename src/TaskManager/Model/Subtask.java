package TaskManager.Model;

public class Subtask extends Task {

    protected Integer epicId; // было idEpic

    public Subtask(String name, String description, Integer epicId) { // Конструктор для создания
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Integer id, Status status) { // Конструктор для обновления
        super(name, description, id, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}