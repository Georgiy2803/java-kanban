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

   /* public Subtask(String name, String description, Integer id, Status status, Integer epicId) { // Конструктор для записи из файла
        super(name, description, id, status);
        this.epicId = epicId;
    }*/


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
        return " \n" + getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", belong Epic=" + epicId ;
    }

    public String toStringForSaving() { // метод сохранения задачи в строку
        return id + "," + "SUBTASK" + "," + name + "," + status + "," + description + "," + epicId;

    }

}