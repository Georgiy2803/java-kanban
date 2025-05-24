package managers.task;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {
    // 2a. Получение списка всех задач
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtask();

    // 2b. Удаление всех задач.
    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSubtask();

    // 2c. Получение по идентификатору.
    Optional<Task> getTaskById(int id);

    Optional<Epic> getEpicById(int id);

    Optional<Subtask> getSubtaskById(int id);

    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    Task createTask(Task inputTask);

    Epic createEpic(Epic inputEpic);

    Subtask createSubtask(Subtask inputSubtask);

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task inputTask);

    void updateEpic(Epic inputEpic);

    void updateSubtask(Subtask inputSubtask);

    // 2f. Удаление по идентификатору.
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    ArrayList<Subtask> listOfEpicSubtasks(int idEpic);

    // 5-й спринт

    List<Task> getHistory(); // должен возвращать последние 10 просмотренных задач

}

