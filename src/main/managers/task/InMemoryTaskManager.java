package managers.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import managers.history.HistoryManager;
import model.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> taskMap;
    protected HashMap<Integer, Epic> epicMap;
    protected HashMap<Integer, Subtask> subtaskMap;
    protected int id = 0; // поле идентификатора
    private HistoryManager historyManager; // просмотренные задачи
    private TreeSet<Task> sortedTasks; // Список задач отсортированных по startTime


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskMap = new HashMap<>(); // создали объект
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        sortedTasks = new TreeSet<>(Comparator
                .comparing(Task::getStartTime, Comparator.nullsFirst(LocalDateTime::compareTo)) // сначала сравниваем startTime, позволяя null стоять первыми
                .thenComparingInt(Task::getId)); // затем сравниваем по Id

    }

    // 5-й спринт
    @Override
    public List<Task> getHistory() { // должен возвращать просмотренные задачи
        return historyManager.getHistory();
    }

    // 8-ой спринт
    @Override
    public List<Task> getPrioritizedTasks() { // возвращает все отсортирванные задачи по startTime и id
        return new ArrayList<>(sortedTasks);
    }

    // Метод для проверки пересечения задачи - Используется перегрузка
    private boolean checkInterseption(Task targetTask, Task taskIsInList) {
        if (taskIsInList.getTaskType() == TaskType.EPIC) { // если задача Эпик, проверку не производим
            return false;
        }
        if (targetTask == taskIsInList) { // задача не пересекается сама с собой.
            return false;
        }
        return !(targetTask.getEndTime().isBefore(taskIsInList.getStartTime())) && // если конец первой задачи наступает раньше, чем начинается вторая задача
                !(taskIsInList.getEndTime().isBefore(targetTask.getStartTime())); // если начало первой задачи происходит позже, чем заканчивается вторая задача
    }

    private boolean checkInterseption(Task targetTask) {
        if (targetTask.getStartTime() != null) { // если у задачи задано время начала
            if (sortedTasks.stream().anyMatch(task -> checkInterseption(targetTask, task))) {
                System.out.println("Задача " + targetTask.getName() + " не была добавлена/обновлена, т.к. пересекается с уже существующими задачами.");
                return true;
            }
        }
        return false;
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
        for (Task task : taskMap.values()) {
            historyManager.remove(task.getId()); // удаление задачи из истории просмотров
            sortedTasks.remove(task); // удаляем Task из sortedTasks, если он там есть
        }
        taskMap.clear(); // удаляем все Task из мапа
    }

    @Override
    public void deleteAllEpic() { // удаление всех Epic и связанных с ними Subtask
        for (Epic epic : epicMap.values()) {
            if (!epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список где хранятся Подзадачи
                for (Integer idSubtask : epic.getListSubtaskIds()) {
                    sortedTasks.remove(subtaskMap.get(idSubtask)); // удаляем Subtask из sortedTasks, если он там есть
                    subtaskMap.remove(idSubtask);
                    historyManager.remove(idSubtask);
                }
            }
            historyManager.remove(epic.getId());
            sortedTasks.remove(epic); // удаляем Epic из sortedTasks, если он там есть
        }
        epicMap.clear();
    }

    @Override
    public void deleteAllSubtask() { // Удаление всех Subtask. Очистка списков у Эпиков и обновление их статуса
        for (Subtask subtask : subtaskMap.values()) {
            historyManager.remove(subtask.getId());
            sortedTasks.remove(subtask); // удаляем Subtask из sortedTasks, если он там есть
        }
        for (Subtask subtask : getSubtask()) {
            int idEpic = subtask.getEpicId(); // получаем id Epic к которому привязаны
            Epic epic = epicMap.get(idEpic); // находим Эпик в мапе
            if (epic != null) {
                if (!epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список
                    epic.getListSubtaskIds().clear(); // очищаем список
                }
                updateEpicStatus(idEpic); // Обновление статуса Эпика
            }
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
    public Task createTask(Task inputTask) { // создание Task
        if (checkInterseption(inputTask)) { // проверяем, не пересекается ли она с другими задачами по времени
            getNextId(); // увеличиваем счётчик на 1, чтобы при добавлении Эпика и подзадач не выпадала ошибка с неверным id
            return null;
        }
        inputTask.setId(getNextId());
        taskMap.put(id, inputTask);
        if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputTask); // добавляем в sortedTasks
        }
        return inputTask;
    }

    @Override
    public Epic createEpic(Epic inputEpic) { // создание Epic
        inputEpic.setId(getNextId());
        epicMap.put(id, inputEpic);
        return inputEpic;
    }

    @Override
    public Subtask createSubtask(Subtask inputSubtask) { // создание Subtask
        if (checkInterseption(inputSubtask)) { // проверяем, не пересекается ли она с другими задачами по времени
            return null;
        }
        Epic epic = epicMap.get(inputSubtask.getEpicId()); // находим Эпик в мапе (получаем id Epic к которому привязаны)
        if (epic == null) { // проверяем существует ли Эпик
            throw new IllegalArgumentException("Подзадачу не удаётся добавить, т.к. Эпик указан не верно или не существует.");
        }
        inputSubtask.setId(getNextId());
        subtaskMap.put(id, inputSubtask);
        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputSubtask); // добавляем в sortedTasks
        }
        // Обновляем Эпик
        addSubtaskToEpic(inputSubtask); // записывает в Эпик номер id Subtask к которому он привязан
        updateEpicDateTime(inputSubtask.getEpicId()); // записывает в Эпик время начала и завершения

        if (epicMap.get(inputSubtask.getEpicId()).getStartTime() != null) { // проверяем, задано ли время начала у подзадачи
            sortedTasks.add(epic); // добавляем в sortedTasks
        }
        return inputSubtask;
    }

    //метод, записывает в Эпик (в список подзадач которые в него входят) номер id Subtask к которому он привязан
    private void addSubtaskToEpic(Subtask inputSubtask) {
        int epicId = inputSubtask.getEpicId();  // узнаём id Эпик к которому привязан Subtask
        Epic epic = epicMap.get(epicId); // Находим Эпик по id
        if (epic != null) { // Проверка, что такой Эпик существует
            epic.getListSubtaskIds().add(inputSubtask.getId()); // добавляем id Subtask в Список Эпика
        }
        updateEpicStatus(epicId);
    }

    // метод, записывает в Эпик время начала и завершения исходя из подзадач
    private void updateEpicDateTime(int epicId) {
        Epic epic = epicMap.get(epicId);

        //Время начала — дата старта самой ранней подзадачи
        Optional<LocalDateTime> startTime = listOfEpicSubtasks(epicId).stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        //время завершения — время окончания самой поздней из задач
        Optional<LocalDateTime> getEndTime = listOfEpicSubtasks(epicId).stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        // Обновляем соответствующие поля у эпика
        startTime.ifPresent(epic::setStartTime); // Метод ifPresent() автоматически вызовет метод setStartTime() только
        getEndTime.ifPresent(epic::setEndTime); // в том случае, если значение в Optional присутствует.

    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public Optional<Task> updateTask(Task inputTask) {
        if (taskMap.get(inputTask.getId()) == null) { // если объекта не существует, производим выход
            return Optional.empty();
        }
        if (checkInterseption(inputTask)) { // проверяем, не пересекается ли она с другими задачами по времени
            return Optional.empty();
        }
        sortedTasks.remove(taskMap.get(inputTask.getId())); // Удаляем старый объект в sortedTasks
        if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputTask); // добавляем в sortedTasks обновленный объект
        }
        int oldId = inputTask.getId();
        taskMap.put(oldId, inputTask);
        return Optional.of(inputTask);
    }

    @Override
    public Optional<Epic> updateEpic(Epic inputEpic) {
        if (epicMap.get(inputEpic.getId()) == null) { // если объекта не существует, производим выход
            return Optional.empty();
        }

        int oldId = inputEpic.getId(); // берём id старого Эпика
        Epic oldEpic = epicMap.get(oldId); // находим старый Эпик в мапе
        for (int subtaskId : oldEpic.getListSubtaskIds()) { // Копируем id связанных Подзадач в новый Эпик
            inputEpic.addSubtaskId(subtaskId);
        }
        inputEpic.setStatus(oldEpic.getStatus()); // вносим в новый Эпик Status старого (изменяемого)
        sortedTasks.remove(oldEpic); // Удаляем старый объект в sortedTasks
        epicMap.put(oldId, inputEpic); // Сохраняем обновлённый Эпик
        updateEpicDateTime(oldId); // записывает в Эпик время начала и завершения
        sortedTasks.add(epicMap.get(oldId)); // добавляем в sortedTasks обновленный Эпик

        return Optional.of(inputEpic);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask inputSubtask) {
        if (subtaskMap.get(inputSubtask.getId()) == null) { // если объекта не существует, производим выход
            return Optional.empty();
        }
        if (checkInterseption(inputSubtask)) { // проверяем, не пересекается ли она с другими задачами по времени
            return Optional.empty();
        }
        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.remove(subtaskMap.get(inputSubtask.getId())); // Удаляем старый объект в sortedTasks
            sortedTasks.remove(epicMap.get(subtaskMap.get(inputSubtask.getId()).getEpicId())); // Удаляем старый объект в sortedTasks
        }

        int oldId = inputSubtask.getId();
        Subtask oldSubtask = subtaskMap.get(oldId); // находим старый Subtask в мапе
        inputSubtask.setEpicId(oldSubtask.getEpicId()); // вносим новый Subtask epicId старого (изменяемого)
        subtaskMap.put(oldId, inputSubtask);

        updateEpicStatus(oldSubtask.getEpicId()); // Обновление статуса Эпика
        updateEpicDateTime(oldSubtask.getEpicId()); // записываем в Эпик время начала и завершения

        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputSubtask); // добавляем в sortedTasks обновленный объект
            sortedTasks.add(epicMap.get(oldSubtask.getEpicId())); // добавляем в sortedTasks обновленный объект
        }
        return Optional.of(inputSubtask);
    }

    // Обновление статуса Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        // Используем stream для подсчета статусов подзадач
        double sum = listOfEpicSubtasks(epicId).stream()
                .mapToDouble(subtask -> {
                    Status status = subtask.getStatus();
                    if (status.equals(Status.DONE)) {
                        return 1.0; // если задача завершена, считаем её целиком
                    } else if (status.equals(Status.IN_PROGRESS)) {
                        return 0.5; // если задача в процессе, считаем половину
                    }
                    return 0.0; // остальные случаи (NEW) считаются ноль баллов
                })
                .sum(); // суммируем баллы

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
        sortedTasks.remove(taskMap.get(id)); // удаляем Task из sortedTasks, если он там есть
        historyManager.remove(taskMap.get(id).getId()); //id
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) { // deleteEpicById
        if (epicMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        sortedTasks.remove(epicMap.get(id)); // удаляем Эпик из sortedTasks
        Epic epic = epicMap.get(id); // находим Эпик в мапе

        // Удаляем все подзадачи с помощью Stream API
        epic.getListSubtaskIds().stream()
                .forEach(subtaskId -> {
                    sortedTasks.remove(subtaskMap.get(subtaskId)); // удаляем подзадачу из sortedTasks, если она там есть
                    subtaskMap.remove(subtaskId);     // удаляем подзадачу из mапа
                    historyManager.remove(subtaskId); // удаляем историю подзадачи
                });

        historyManager.remove(epicMap.get(id).getId()); // удаляем Эпик из истории просмотров
        epicMap.remove(id); // удаляем Эпик из мапа
    }


    @Override
    public void deleteSubtaskById(int id) { // deleteSubtaskById
        if (subtaskMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        sortedTasks.remove(subtaskMap.get(id)); // удаляем Subtask из sortedTasks, если она там есть

        int idEpic = subtaskMap.get(id).getEpicId(); // получаем id Epic к которому привязан Subtask
        Epic epic = epicMap.get(idEpic); // находим Эпик в мапе

        // Используем стрим для удаления id подзадачи из списка подзадач Эпика
        epic.getListSubtaskIds().removeIf(subtaskId -> subtaskId == id);

        historyManager.remove(subtaskMap.get(id).getId()); // удаляем из истории просмотров
        subtaskMap.remove(id);    // удаляем Подзадачу
        updateEpicStatus(idEpic); // обновляем Эпик
    }

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    @Override
    public ArrayList<Subtask> listOfEpicSubtasks(int idEpic) { // метод переделан через .stream()
        Epic epic = epicMap.get(idEpic); // Находим Эпик по id
        if (epic == null) { // Проверка, если такого Эпика не существует
            return new ArrayList<>(); // Возвращаем пустой ArrayList, если эпик не найден
        }

        return epic.getListSubtaskIds().stream()                   // Берём список id подзадач Эпика
                .map(subtaskMap::get)                              // Преобразовываем id в сами подзадачи
                .filter(Objects::nonNull)                          // Фильтруем удалённые или отсутствующие подзадачи
                .collect(Collectors.toCollection(ArrayList::new)); // Собираем результат в ArrayList
    }

}
