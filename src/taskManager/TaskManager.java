package taskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import taskManager.model.Task;
import taskManager.model.Epic;
import taskManager.model.Subtask;
import taskManager.model.Status;


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
    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    public List<Subtask> getSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

    // 2b. Удаление всех задач.
    public void deleteAllTasks() { // удаление всех Task
        taskMap.clear();
    }

    public void deleteAllEpic() { // удаление всех Epic и связанных с ними Subtask
        epicMap.clear();
        subtaskMap.clear();
    }

    public void deleteAllSubtask() { // Удаление всех Subtask. Очистка списков у Эпиков и обновление их статуса
        for (Subtask subtask : getSubtask()) {
            int idEpic = subtask.getEpicId(); // получаем id Epic к которому привязаны
            Epic epic = epicMap.get(idEpic); // находим Эпик в мапе
            if (!epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список
                epic.getListSubtaskIds().clear(); // очищаем список
            }
            updateEpicStatus(idEpic); // Обновление статуса Эпика
        }
        subtaskMap.clear(); // удаляем все Subtask
    }

    // 2c. Получение по идентификатору.
    public Optional<Task> getTaskById(int id) { // getTaskById
        return Optional.ofNullable(taskMap.get(id));

    }

    public Optional<Epic> getEpicById(int id) {
        return Optional.ofNullable(epicMap.get(id));
    }

    public Optional<Subtask> getSubtaskById(int id) {
        return Optional.ofNullable(subtaskMap.get(id));
    }


    private int getNextId() {
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

    public Subtask createSubtask(Subtask inputSubtask) { // создание Subtask
        Epic epic = epicMap.get(inputSubtask.getEpicId()); // находим Эпик в мапе (получаем id Epic к которому привязаны)
        if (epic == null) {
            return null;
        }
        inputSubtask.setId(getNextId());
        subtaskMap.put(id, inputSubtask);
        addSubtaskToEpic(inputSubtask); // записывает в Эпик номер id Subtask к которому он привязан
        return inputSubtask;
    }

    //метод, записывает в Эпик (в список подзадач которые в него входят) номер id Subtask к которому он привязан
    private void addSubtaskToEpic(Subtask inputSubtask) {
        int epicId = inputSubtask.getEpicId();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicMap.get(epicId); // Находим Эпик по id
        if (epic != null) { // Проверка, если такого Эпика не существует
            epic.getListSubtaskIds().add(inputSubtask.getId()); // добавляем id Subtask в Список Эпика
        }
        updateEpicStatus(epicId);
    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(Task inputTask) {
        int oldId = inputTask.getId();
        taskMap.put(oldId, inputTask);
        return inputTask;
    }

    public Epic updateEpic(Epic inputEpic) {
        int oldId = inputEpic.getId();
        Epic oldEpic = epicMap.get(oldId); // находим старый Эпик в мапе
        inputEpic.setListSubtaskIds(oldEpic.getListSubtaskIds()); // вносим новый Эпик listSubtaskIds старого (изменяемого)
        inputEpic.setStatus(oldEpic.getStatus()); // вносим новый Эпик Status старого (изменяемого)
        epicMap.put(oldId, inputEpic); // Сохраняем обновлённый Эпик
        return inputEpic;
    }


    public Subtask updateSubtask(Subtask inputSubtask) {
        int oldId = inputSubtask.getId();
        Subtask oldSubtask = subtaskMap.get(oldId); // находим старый Subtask в мапе
        inputSubtask.setEpicId(oldSubtask.getEpicId()); // вносим новый Subtask epicId старого (изменяемого)
        subtaskMap.put(oldId, inputSubtask);
        updateEpicStatus(oldSubtask.getEpicId()); // Обновление статуса Эпика
        return inputSubtask;
    }


    // Обновление статуса Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        double sum = 0; // количество завершённых подзадач у Эпика
        for (Subtask subtask : listOfEpicSubtasks(epicId)) {
            Status status = subtask.getStatus(); // Вытаскиваем у Subtask его Статус
            if (status.equals(Status.DONE)) {  // Проверяем Статус, если завершён, то..
                sum = sum + 1; // прибавляем 1
            } else if (status.equals(Status.IN_PROGRESS)) { // если в "в процессе" то..
                sum = sum + 0.5; // прибавляем 0,5
            }
        }
        if (sum == 0) { // если все подзадачи новые или их нет
            epic.setStatus(Status.NEW);  // присваиваем Эпику Статус - Новое.
        } else if (sum == epic.getListSubtaskIds().size()) { // если все подзадачи завершены
            epic.setStatus(Status.DONE); // присваиваем Эпику Статус - Завершён.
        } else { // если выполнена хоть одна подзадача не завершена или в процессе
            epic.setStatus(Status.IN_PROGRESS); // присваиваем Эпику Статус - В процессе.
        }
    }

    // 2f. Удаление по идентификатору.
    public void deleteByIdTask(int id) {
        taskMap.remove(id);
    }

    public void deleteByIdEpic(int id) {
        Epic Epic = epicMap.get(id); // находим Эпик в мапе
        if (!Epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список где хранятся Подзадачи
            for (Integer idSubtask : Epic.getListSubtaskIds()) {
                subtaskMap.remove(idSubtask);
            }
        }
        epicMap.remove(id);
    }

    public void deleteByIdSubtask(int id) {
        int idEpic = subtaskMap.get(id).getEpicId(); // получаем id Epic к которому привязан Subtask
        Epic epic = epicMap.get(idEpic); // находим Эпик в мапе
        for (int i = 0; i < epic.getListSubtaskIds().size(); i++) {
            if (epic.getListSubtaskIds().get(i) == id) { // находим индекс подзадачи
                epic.getListSubtaskIds().remove(i); // удаляем id подзадачи
            }
        }
        subtaskMap.remove(id); // удаляем Подзадачу
        updateEpicStatus(idEpic); // обновляем Эпик
    }


    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    public ArrayList<Subtask> listOfEpicSubtasks(int idEpic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>(); // создаем список
        Epic epic = epicMap.get(idEpic); // Находим Эпик по id
        if (epic != null) { // Проверка, если такого Эпика не существует
            for (int subtaskId : epic.getListSubtaskIds()) { // проходимся по списку подзадач Эпика
                Subtask subtask = subtaskMap.get(subtaskId); // Находим Subtask
                listSubtasks.add(subtask); // добавляем subtask в список
            }
        }
        return listSubtasks;
    }

}
