package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Cloneable  {
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;
    protected TaskType taskType = TaskType.TASK; // задаём значение по умолчанию
    protected LocalDateTime startTime; // дата и время, когда предполагается приступить к выполнению задачи
    protected Duration duration; //  продолжительность задачи, оценка того, сколько времени она займёт в минутах

    public Task(String name, String description) { // Конструктор для создания задачи
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    // Конструктор для создания с меткой времени
    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Конструктор для обновления и записи из файла
    public Task(String name, String description, Integer id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    // Конструктор для обновления с меткой времени
    public Task(String name, String description, Integer id, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Integer id) { // Конструктор для обновления Эпика
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(Task original) { // Конструктор для создания копии объекта
        this.name = original.name;
        this.description = original.description;
        this.id = original.id;
        this.status = original.status;
    }

    // дата и время завершения задачи - рассчитывается исходя из startTime и duration.
    public LocalDateTime getEndTime() {
        if (startTime != null && duration != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public Object clone() throws CloneNotSupportedException { // для создании копии объекта в InMemoryHistoryManager
        return super.clone();
    }

    @Override
    public String toString() {
        return "\n" + taskType +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", начало задачи= " + (startTime != null ? startTime.format(formatter) : "не установлено") +
                ", конец задачи= " + (getEndTime() != null ? getEndTime().format(formatter) : "не определён");
    }
}