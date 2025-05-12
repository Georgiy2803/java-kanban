package main.model;

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


    // решил выполнить 3-ю рекомендацию, т.к. первые две требовали слишком большого изменения кода, врезультате чего,
    // появились бы новые ошибки. 2-я мне понравилась, но не знаком с созданием приватных конструторов, а то как его
    // добавить на этапе добавления в менеджер тасков вызывает у меня страх, что я слишком затяну со сдачей проекта.
    public void setEpicId(Integer epicId) {
        if (getId() == epicId){ // проверка, чтобы id Эпика нельзя было добавить в список, где хранятся Подзадачи
            return;
        }this.epicId = epicId;
    }
}