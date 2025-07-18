package managers.task;

import exception.IntersectionException;
import exception.NotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;

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
    Optional<Task> getTaskById(int id) throws NotFoundException;

    Optional<Epic> getEpicById(int id) throws NotFoundException;

    Optional<Subtask> getSubtaskById(int id) throws NotFoundException;

    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    Task createTask(Task inputTask) throws IntersectionException;

    Epic createEpic(Epic inputEpic);

    Subtask createSubtask(Subtask inputSubtask) throws IntersectionException;

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    Optional<Task> updateTask (Task inputTask) throws NotFoundException, IntersectionException;

    Optional<Epic> updateEpic (Epic inputEpic) throws NotFoundException;

    Optional<Subtask> updateSubtask (Subtask inputSubtask) throws NotFoundException, IntersectionException;

    // 2f. Удаление по идентификатору.
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    List<Subtask> listOfEpicSubtasks(int idEpic);

    // 5-й спринт

    List<Task> getHistory(); // должен возвращать просмотренные задачи

    List<Task> getPrioritizedTasks();  // возвращает все отсортирванные задачи по приоритету — то есть по startTime


}

