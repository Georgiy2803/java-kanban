import java.util.HashMap;
import java.util.Optional;

public class TaskManager {

    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, Subtask> subtaskList;
    private int id = 0; // поле идентификатора
    /* Вопрос к ревьюеру.
    А какой в данном случае лучше использовать модификатор переменных? (private / public / protected / static)
    Правильно ли я выбрал их в данном случае?
     */

    public TaskManager() {
        taskList = new HashMap<Integer, Task>(); // создали объект
        /* Вопрос к ревьюеру.
        1. Создать объект можно было и в поле, а не конструкторе. Но как принято это у опытных программистов?
        2. Объект можно создать и так " taskList = new HashMap<>(); ", но как лучше(или более зрело) и почему? */
        epicList = new HashMap<Integer, Epic>();
        subtaskList = new HashMap<Integer, Subtask>();
    }

    // 2a. Получение списка всех задач
    public void getAllTasks() {
        getTasks();
        getEpics();
        getSubtask();
        /* Вопрос к ревьюеру.
    Ниже строчки, нужны для выполнения задания "Проверка через Main". Однако я никак не смог реализовать печать этого
    метода void в Main. Подскажите, как это можно сделать, чтобы здесь остались только вызовы методов возвращения объектов.
     */
        System.out.println(getTasks());
        System.out.println(getEpics());
        System.out.println(getSubtask());
    }

    public HashMap<Integer, Task> getTasks() {
        return taskList;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epicList;
    }

    public HashMap<Integer, Subtask> getSubtask() {
        return subtaskList;
    }

    // 2b. Удаление всех задач.
    public void allDeleteTasks() {
        taskList.clear();
        epicList.clear();
        subtaskList.clear();
    }

    // 2c. Получение по идентификатору.
    public void getAllTasksId(int id) {
        getTasksId(id);
        getEpicsId(id);
        getSubtasksId(id);
        if (!getTasksId(id).equals(Optional.empty())) {
            System.out.println(getTasksId(id));
        } else if (!getEpicsId(id).equals(Optional.empty())) {
            System.out.println(getEpicsId(id));
        } else if (!getSubtasksId(id).equals(Optional.empty())) {
            System.out.println(getSubtasksId(id));
        }
    }

    /* Вопрос к ревьюеру.
    Рационально ли здесь использовать Optional<> и вообще производить проверку на null. Без этой проверки всё работает
    замечательно.
     */
    public Optional<Task> getTasksId(int id) {
        if (taskList.get(id) != null) {
            return Optional.of(taskList.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Epic> getEpicsId(int id) {
        if (epicList.get(id) != null) {
            return Optional.of(epicList.get(id));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Subtask> getSubtasksId(int id) {
        if (subtaskList.get(id) != null) {
            return Optional.of(subtaskList.get(id));
        } else {
            return Optional.empty();
        }
    }

    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task createTask(Task intask) {
        id++;
        intask.setId(id);
        taskList.put(id, intask);
        return intask;
    }

    public Epic createEpic(Epic inEpic) {
        id++;
        inEpic.setId(id);
        epicList.put(id, inEpic);
        return inEpic;
    }

    public Subtask createSubtask(Subtask inSubtask) { // метод из пачки
        id++;
        inSubtask.setId(id);
        subtaskList.put(id, inSubtask);
        addInEpicIdSubtask(inSubtask); // вызов метода, где присваивается номер id Subtask - Эпику к которому он привязан
        return inSubtask;
    }

    //метод, где присваивается номер id Subtask - Эпику к которому он привязан
    public void addInEpicIdSubtask(Subtask inSubtask) {
        int EpicId = inSubtask.getIdEpic();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicList.get(EpicId); // Находим Эпик по id
        epic.idSubtask.add(inSubtask.id); // добавляем id Subtask в Список Эпика
    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updatingTask(Task intask) {
        int oldId = intask.getId();
        taskList.put(oldId, intask);
        return intask;
    }

    public Epic updatingEpic(Epic inEpic) {
        int oldId = inEpic.getId();
        epicList.put(oldId, inEpic);
        return inEpic;
    }

    public Subtask updatingSubtask(Subtask inSubtask) {
        int oldId = inSubtask.getId();
        subtaskList.put(oldId, inSubtask);
        updatingEpicStatus(inSubtask); // обновление статуса Эпика
        return inSubtask;
    }

    // Обновление статуса Эпика
    public void updatingEpicStatus(Subtask inSubtask) {
        int EpicId = inSubtask.getIdEpic();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicList.get(EpicId); // Находим Эпик по id
        double sum = 0; // количество завершённых подзадач у Эпика
        for (int id : epic.idSubtask) { // проходимся по списку подзадач Эпика
            Subtask subtask = subtaskList.get(id); // Находим Subtask
            Status status = subtask.status; // Вытаскиваем у Subtask его Статус
            if (status.equals(Status.DONE)) {  // Проверяем Статус, если завершён, то..
                sum++; // прибавляем 1
            } else if (status.equals(Status.IN_PROGRESS)) { // если в "в процессе" то..
                sum = sum + 0.5; // прибавляем 0,5
            }
            if (sum == epic.idSubtask.size()) { // если все подзадачи завершены
                epic.status = Status.DONE; // присваиваем Эпику Статус - Завершён.
            } else if (sum >= 1) { // если выполнена хоть одна подзадача не завершена или в процессе
                epic.status = Status.IN_PROGRESS; // присваиваем Эпику Статус - В процессе.
            } else {
                epic.status = Status.NEW; // присваиваем Эпику Статус - Новое.
            }
        }
    }

    // 2f. Удаление по идентификатору.
    public void deleteById(int id) {
        taskList.remove(id);
        epicList.remove(id);
        subtaskList.remove(id);
    }

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    public void ListOfEpicSubtasks(int id) {
        Epic epic = epicList.get(id); // Находим Эпик по id
        for (int i : epic.idSubtask) { // проходимся по списку подзадач Эпика
            Subtask subtask = subtaskList.get(i); // Находим Subtask
            System.out.println(returnSubtask(subtask)); // печать полученного объекта
        }
    }

    // Метод возвращает то, что принял. Чтобы void мог вернуть объект несколько раз через for
    public Subtask returnSubtask(Subtask inSubtask) {
        return inSubtask;
    }
}
