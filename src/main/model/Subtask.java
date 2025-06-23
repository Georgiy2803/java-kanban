package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    protected Integer epicId;

    public Subtask(String name, String description, Integer epicId) { // Конструктор для создания
        super(name, description);
        this.epicId = epicId;
    }

    // Конструктор для создания с меткой времени
    public Subtask(String name, String description, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
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
        return " \n" + getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", принадлежит эпику=" + epicId +
                ", начало подзадачи= " + (startTime != null ? startTime.format(formatter) : "не установлено") +
                ", конец подзадачи= " + (startTime != null && duration != null ? getEndTime(duration, startTime).format(formatter) : "не определён");

    }
}