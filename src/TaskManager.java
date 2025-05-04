import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import Task.Task;
import Task.Epic;
import Task.Subtask;
import Task.Status;


public class TaskManager {

    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;
    private int id = 0; // поле идентификатора


    public TaskManager() {
        taskMap = new HashMap<Integer, Task>(); // создали объект
        epicMap = new HashMap<Integer, Epic>();
        subtaskMap = new HashMap<Integer, Subtask>();
    }

    // 2a. Получение списка всех задач
    /* Комментарий ревьюера - тут должны быть методы, которые возврашают не мапы, а какую ни будь коллекцию.

    Вопрос - что значить "какую ни будь коллекцию"? О чём идёт речь? Про ArrayList<Subtask> например?
    п.с. вроде говорилось, что я должен возвращать объект */
    public HashMap<Integer, Task> getTasks() {
        return taskMap;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epicMap;
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtaskMap;
    }

    /* Комментарий ревьюера - снова, должно быть три отдельных метода, потому что удаление каждого из типов тасков
    происходит по-разному.

    Вопрос - Как это по-разному? Это же один тип хеш-таблиц (с не много разными типами в value) и очищаются
    одной и той же командой. */

    // 2b. Удаление всех задач.
    /*public void allDeleteTasks() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }*/

    public void allDeleteTask() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }
    public void allDeleteEpic() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }
    public void allDeleteSubtask() {
        taskMap.clear();
        epicMap.clear();
        subtaskMap.clear();
    }

    // 2c. Получение по идентификатору.
    /* Вопрос к ревьюеру.
    Рационально ли здесь использовать Optional<> и вообще производить проверку на null. Без этой проверки всё работает
    замечательно.
     */
    public Optional<Task> getTaskById(int id) { // getTaskById
        if (taskMap.get(id) != null) {
            return Optional.of(taskMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Epic> getEpicById(int id) {
        if (epicMap.get(id) != null) {
            return Optional.of(epicMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Subtask> getSubtaskById(int id) {
        if (subtaskMap.get(id) != null) {
            return Optional.of(subtaskMap.get(id));
        } else {
            return Optional.empty();
        }
    }


    private int getNextId(){
        return ++id; // Освежил воспоминания по разнице между i++ и ++i. Здесь это актуально. Спасибо
    }
    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task createTask(Task inputTask) {
        inputTask.setId(getNextId());
        taskMap.put(id, inputTask);
        return inputTask;
    }

    public Epic createEpic(Epic inputEpic) {
        inputEpic.setId(getNextId());
        epicMap.put(id, inputEpic);
        return inputEpic;
    }

    public Subtask createSubtask(Subtask inputSubtask) { // метод из пачки
        inputSubtask.setId(getNextId());
        subtaskMap.put(id, inputSubtask);
        addSubtaskToEpic(inputSubtask); // записывает в Эпик номер id Subtask к которому он привязан
        return inputSubtask;
    }

    //метод, записывает в Эпик (в список подзадач которые в него входят) номер id Subtask к которому он привязан
    public void addSubtaskToEpic(Subtask inputSubtask) {
        int EpicId = inputSubtask.getEpicId();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicMap.get(EpicId); // Находим Эпик по id
        if(epic != null) // Проверка, если такого Эпика не существует
        epic.getsubtaskIds().add(inputSubtask.getId()); // добавляем id Subtask в Список Эпика
    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(Task inputTask) {
        int oldId = inputTask.getId();
        taskMap.put(oldId, inputTask);
        return inputTask;
    }

    public Epic updatingEpic(Epic inputEpic) {
        int oldId = inputEpic.getId();
        epicMap.put(oldId, inputEpic);
        return inputEpic;
    }

    public Subtask updatingSubtask(Subtask inputSubtask) {
        int oldId = inputSubtask.getId();
        subtaskMap.put(oldId, inputSubtask);
        if(epicMap.get(inputSubtask.getEpicId()) !=null) // Проверка, если такого Эпика не существует
            updateEpicStatus(inputSubtask); // обновление статуса Эпика
        return inputSubtask;
    }

    // Обновление статуса Эпика
    public void updateEpicStatus(Subtask inputSubtask) {
        int epicId = inputSubtask.getEpicId();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicMap.get(epicId); // Находим Эпик по id
        double sum = 0; // количество завершённых подзадач у Эпика
        for (int id : epic.getsubtaskIds()) { // проходимся по списку подзадач Эпика
            Subtask subtask = subtaskMap.get(id); // Находим Subtask
            Status status = subtask.getStatus(); // Вытаскиваем у Subtask его Статус
            if (status.equals(Status.DONE)) {  // Проверяем Статус, если завершён, то..
                sum++; // прибавляем 1
            } else if (status.equals(Status.IN_PROGRESS)) { // если в "в процессе" то..
                sum = sum + 0.5; // прибавляем 0,5
            }
        }
        if (sum == epic.getsubtaskIds().size()) { // если все подзадачи завершены
            epic.setStatus(Status.DONE); // присваиваем Эпику Статус - Завершён.
        } else if (sum >= 1) { // если выполнена хоть одна подзадача не завершена или в процессе
            epic.setStatus(Status.IN_PROGRESS); // присваиваем Эпику Статус - В процессе.
        } else {
            epic.setStatus(Status.NEW); // присваиваем Эпику Статус - Новое.
        }
    }

    // 2f. Удаление по идентификатору.
    public void deleteByIdTask(int id) {
        taskMap.remove(id);
    }
    public void deleteByIdEpic(int id) {
        epicMap.remove(id);
    }
    public void deleteByIdSubtask(int id) {
        subtaskMap.remove(id);
    }

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    public ArrayList<Subtask> listOfEpicSubtasks(int idEpic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>(); // создаем список
        Epic epic = epicMap.get(idEpic); // Находим Эпик по id
        if(epic != null)  // Проверка, если такого Эпика не существует
        for (int i : epic.getsubtaskIds()) { // проходимся по списку подзадач Эпика
            Subtask subtask = subtaskMap.get(i); // Находим Subtask
            listSubtasks.add(subtask); // добавляем subtask в список
        }
        return listSubtasks;
    }

}
