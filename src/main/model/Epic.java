package main.model;

import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> listSubtaskIds = new ArrayList<>(); // subtaskIds было idSubtask

    public Epic(String name, String description) { // Конструктор для создания
        super(name, description);
       // this.status = Status.NEW;
    }

    public Epic(String name, String description, Integer id) { // Конструктор для обновления: наименования и описания
        super(name, description, id);
        this.status = Status.NEW;
    }

    public ArrayList<Integer> getListSubtaskIds() {
        return listSubtaskIds;
    }

    // решил добавить логику проверки в этот метод, чтобы не рушить другие конструкции
    // Идеальный вариант не смог сделать. Не понял самому логику совета
    public void setListSubtaskIds(ArrayList<Integer> subtaskIds) {
        for (int subtaskId :listSubtaskIds){
            if (getId() == subtaskId){ // проверка, чтобы id Эпика нельзя было добавить в список, где хранятся Подзадачи
                return;
            }
        }
        this.listSubtaskIds = subtaskIds;
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