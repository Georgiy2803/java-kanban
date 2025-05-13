package managers.history;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager { // Реализация истории просмотров в памяти

    private List<Task> history = new ArrayList<>();

    public static final int HISTORY_MAX_SIZE = 10;


    public void add(Task task) {
        if (history.size() >= HISTORY_MAX_SIZE) {
            history.remove(0);
        }
        try {
            history.add((Task) task.clone()); // добавляет копию объекта
        } catch (CloneNotSupportedException e) {
            // Обработка исключения или логирование
            e.printStackTrace();
        }
    }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}