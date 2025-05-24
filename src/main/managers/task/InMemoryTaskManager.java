package managers.task;

import java.util.*;

import managers.history.HistoryManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;


public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> taskMap;
    private HashMap<Integer, Epic> epicMap;
    private HashMap<Integer, Subtask> subtaskMap;
    private int id = 0; // поле идентификатора

    private HistoryManager historyManager; // стало

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskMap = new HashMap<>(); // создали объект
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
    }

    // 5-й спринт
    @Override
    public List<Task> getHistory() { // должен возвращать последние 10 просмотренных задач
        return historyManager.getHistory();
    }

    // 4-й спринт
    // 2a. Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<Subtask> getSubtask() {
        return new ArrayList<>(subtaskMap.values());
    }

    // 2b. Удаление всех задач.
    @Override
    public void deleteAllTasks() { // удаление всех Task
        for(Task task: taskMap.values()) {
            historyManager.remove(task.getId());
        }
        taskMap.clear();

    }

    @Override
    public void deleteAllEpic() { // удаление всех Epic и связанных с ними Subtask
        for(Epic epic: epicMap.values()) {
            if (!epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список где хранятся Подзадачи
                for (Integer idSubtask : epic.getListSubtaskIds()) {
                    subtaskMap.remove(idSubtask);
                    historyManager.remove(idSubtask);
                }
            }
            historyManager.remove(epic.getId());
        }
        epicMap.clear();
    }




    @Override
    public void deleteAllSubtask() { // Удаление всех Subtask. Очистка списков у Эпиков и обновление их статуса
        for(Subtask subtask: subtaskMap.values()) {
            historyManager.remove(subtask.getId());
        }
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
    @Override
    public Optional<Task> getTaskById(int id) { // getTaskById
        historyManager.add(taskMap.get(id)); // добавляет запрос в историю просмотров
        return Optional.ofNullable(taskMap.get(id));
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        historyManager.add(epicMap.get(id)); // добавляет запрос в историю просмотров
        return Optional.ofNullable(epicMap.get(id));
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        historyManager.add(subtaskMap.get(id)); // добавляет запрос в историю просмотров
        return Optional.ofNullable(subtaskMap.get(id));
    }

    private int getNextId() {
        return ++id; // Освежил воспоминания по разнице между i++ и ++i. Здесь это актуально. Спасибо
    }

    // 2d. Создание. Сам объект должен передаваться в качестве параметра.
    @Override
    public Task createTask(Task inputTask) {
        inputTask.setId(getNextId());
        taskMap.put(id, inputTask);
        return inputTask;
    }

    @Override
    public Epic createEpic(Epic inputEpic) {
        inputEpic.setId(getNextId());
        epicMap.put(id, inputEpic);
        return inputEpic;
    }

    @Override
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
    @Override
    public void updateTask(Task inputTask) {
        if (taskMap.get(inputTask.getId()) == null) { // если объекта не существует, производим выход
            return;
        }
        int oldId = inputTask.getId();
        taskMap.put(oldId, inputTask);
    }

    @Override
    public void updateEpic(Epic inputEpic) {
        if (epicMap.get(inputEpic.getId()) == null) { // если объекта не существует, производим выход
            return;
        }
        int oldId = inputEpic.getId();
        Epic oldEpic = epicMap.get(oldId); // находим старый Эпик в мапе
        for (int subtaskId : oldEpic.getListSubtaskIds()) {
            inputEpic.addSubtaskId(subtaskId);
        }
        inputEpic.setStatus(oldEpic.getStatus()); // вносим в новый Эпик Status старого (изменяемого)
        epicMap.put(oldId, inputEpic); // Сохраняем обновлённый Эпик
    }


    @Override
    public void updateSubtask(Subtask inputSubtask) {
        if (subtaskMap.get(inputSubtask.getId()) == null) { // если объекта не существует, производим выход
            return;
        }
        int oldId = inputSubtask.getId();
        Subtask oldSubtask = subtaskMap.get(oldId); // находим старый Subtask в мапе
        inputSubtask.setEpicId(oldSubtask.getEpicId()); // вносим новый Subtask epicId старого (изменяемого)
        subtaskMap.put(oldId, inputSubtask);
        updateEpicStatus(oldSubtask.getEpicId()); // Обновление статуса Эпика
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
    @Override
    public void deleteTaskById(int id) { // deleteTaskById
        if (taskMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        historyManager.remove(taskMap.get(id).getId());
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById (int id) { // deleteEpicById
        if (epicMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        Epic Epic = epicMap.get(id); // находим Эпик в мапе
        if (!Epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список где хранятся Подзадачи
            for (Integer idSubtask : Epic.getListSubtaskIds()) {
                subtaskMap.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
        }
        historyManager.remove(epicMap.get(id).getId());
        epicMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) { // deleteSubtaskById
        if (subtaskMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        int idEpic = subtaskMap.get(id).getEpicId(); // получаем id Epic к которому привязан Subtask
        Epic epic = epicMap.get(idEpic); // находим Эпик в мапе
        for (int i = 0; i < epic.getListSubtaskIds().size(); i++) {
            if (epic.getListSubtaskIds().get(i) == id) { // находим индекс подзадачи
                epic.getListSubtaskIds().remove(i); // удаляем id подзадачи
            }
        }
        historyManager.remove(subtaskMap.get(id).getId());
        subtaskMap.remove(id); // удаляем Подзадачу
        updateEpicStatus(idEpic); // обновляем Эпик
    }


    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    @Override
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
