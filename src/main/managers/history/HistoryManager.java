package managers.history;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task); // должен помечать задачи как просмотренные

    List<Task> getHistory(); // возвращать их список.

    void remove(int id); // удаление задачи из просмотра
}