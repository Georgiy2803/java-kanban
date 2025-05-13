package model;

import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> listSubtaskIds = new ArrayList<>(); // subtaskIds было idSubtask

    public Epic(String name, String description) { // Конструктор для создания
        super(name, description);
    }

    public Epic(String name, String description, Integer id) { // Конструктор для обновления: наименования и описания
        super(name, description, id);
        this.status = Status.NEW;
    }

    public ArrayList<Integer> getListSubtaskIds() {
        return listSubtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if(subtaskId != getId()) { // проверка, чтобы id Эпика нельзя было добавить в список, где хранятся Подзадачи
            listSubtaskIds.add(subtaskId);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status + ", подзадачи: " + listSubtaskIds + " \n";
    }
}