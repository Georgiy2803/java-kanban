package model;

public class Subtask extends Task {

    protected Integer epicId;

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
        if (getId() == epicId) { // проверка, чтобы id Эпика нельзя было добавить в список, где хранятся Подзадачи
            return;
        }
        this.epicId = epicId;
    }

    @Override
    public void setId(Integer id) {
        if(id == epicId) {
            return;
        }
        super.setId(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", subtasks=" + epicId +
                '}' + "\n";
    }
}