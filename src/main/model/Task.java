package main.model;

public class Task  {
    protected String name;
    protected String description;
    protected Integer id;
    protected Status status;

    public Task(String name, String description) { // Конструктор для создания
        this.name = name;
        this.description = description;
        this.status = Status.NEW;

    }
    public Task(String name, String description, Integer id, Status status) { // Конструктор для обновления
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;

    }

    public Task(String name, String description, Integer id) { // Конструктор для обновления Эпика
        this.name = name;
        this.description = description;
        this.id = id;
    }


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

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + " \n";
    }
}