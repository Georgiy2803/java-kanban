package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class Epic extends Task{

    protected ArrayList<Integer> listSubtaskIds = new ArrayList<>(); // subtaskIds было idSubtask
    protected LocalDateTime endTime; // Рассчитывается. Время завершения — время окончания самой поздней подзадачи

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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addSubtaskId(int subtaskId) {
        if(subtaskId != getId()) { // проверка, чтобы id Эпика нельзя было добавить в список, где хранятся Подзадачи
            listSubtaskIds.add(subtaskId);
        }
    }

    @Override
    public String toString() {
        return " \n" + getClass().getSimpleName() +
                ". name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", входящие подзадачи=" + listSubtaskIds +
                ", начало эпика= " + (startTime != null ? startTime.format(formatter) : "не установлено") +
                ", конец эпика= " + (endTime != null ? endTime.format(formatter) : "не определён");
    }
}