package model;

public class Task implements Cloneable  {
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

    public Task(Task original) { // Конструктор для создания копии объекта
        this.name = original.name;
        this.description = original.description;
        this.id = original.id;
        this.status = original.status;
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
        return getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + " \n";
    }
}