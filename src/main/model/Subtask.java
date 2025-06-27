package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    protected Integer epicId;

    public Subtask(String name, String description, Integer epicId) { // Конструктор для создания
        super(name, description);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    // Конструктор для создания с меткой времени
    public Subtask(String name, String description, Integer epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }


    public Subtask(String name, String description, Integer id, Status status) { // Конструктор для обновления
        super(name, description, id, status);
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, Integer id, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    /*// Конструктор для обновления с меткой времени
    public Subtask(String name, String description, Integer id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
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
        return " \n" + taskType +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", принадлежит эпику=" + epicId +
                ", начало подзадачи= " + (startTime != null ? startTime.format(formatter) : "не установлено") +
                ", конец подзадачи= " + (startTime != null && duration != null ? endTime.format(formatter) : "не определён");

    }
}