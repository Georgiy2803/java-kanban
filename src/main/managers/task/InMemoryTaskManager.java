package managers.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import managers.history.HistoryManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> taskMap;
    protected HashMap<Integer, Epic> epicMap;
    protected HashMap<Integer, Subtask> subtaskMap;
    protected int id = 0; // поле идентификатора
    private HistoryManager historyManager; // просмотренные задачи
    private List<Task> sortedTasks; // Очередь задач, отсортированных по startTime


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskMap = new HashMap<>(); // создали объект
        epicMap = new HashMap<>();
        subtaskMap = new HashMap<>();
        sortedTasks = new ArrayList<>();
    }

    // 5-й спринт
    @Override
    public List<Task> getHistory() { // должен возвращать просмотренные задачи
        return historyManager.getHistory();
    }

    // 8-ой спринт
    @Override
    public List<Task> getPrioritizedTasks() { // возвращает все отсортирванные задачи по приоритету — то есть по startTime
        return new ArrayList<>(sortedTasks);
    }

    private void sortTasks() {
        sortedTasks.sort(Comparator.comparing(Task::getStartTime));
    }

    public boolean timeChecker(Task targetTask, Task taskIsInList) {
        if (taskIsInList.getClass().getSimpleName().equals("Epic")){ // если задача Эпик, проверку не производим
            return false; // выходим
        }
        // Если конец первой задачи наступает раньше, чем начинается вторая задача
        if (targetTask.getEndTime(targetTask.getDuration(), targetTask.getStartTime()).isBefore(taskIsInList.getStartTime())) {
            return false; // не пересекаются
        }
        // Если начало первой задачи происходит позже, чем заканчивается вторая задача
        if (taskIsInList.getEndTime(taskIsInList.getDuration(), taskIsInList.getStartTime()).isBefore(targetTask.getStartTime())) {
            return false; // не пересекаются
        }
        // В остальных случаях задачи пересекаются
        return true; // пересекаются
    }

    // Метод для проверки пересечения задачи с любой другой задачей в taskMap
    public boolean timeCheckerMap(Task targetTask) {
        // anyMatch — это метод в Java Stream API, который используется для проверки, удовлетворяет ли хотя бы один
        // элемент потока заданному условию. Метод возвращает true, если хотя бы один элемент соответствует условию,
        // и false, если ни один элемент не соответствует.
        return sortedTasks.stream()
                .anyMatch(task -> timeChecker(targetTask, task));
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
            historyManager.remove(task.getId());
        }
        taskMap.clear();

    }

    @Override
    public void deleteAllEpic() { // удаление всех Epic и связанных с ними Subtask
        for (Epic epic : epicMap.values()) {
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
        for (Subtask subtask : subtaskMap.values()) {
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
    /*public Task createTask(Task inputTask) {
        if (inputTask.getStartTime() != null) { // если у задачи задано время начала
            if (timeCheckerMap(inputTask)) { // то проверяем не пересекается ли она с другими задачами по времени
                throw new IllegalArgumentException("Новая задача пересекается с уже существующими задачами.");
            }
        }
        inputTask.setId(getNextId());
        taskMap.put(id, inputTask);
        if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputTask); // тогда добавляем в sortedTasks
            sortTasks(); // и сортируем список после добавления по startTime
        }
        return inputTask;
    }*/

    public Task createTask(Task inputTask) {
        try {
            if (inputTask.getStartTime() != null) { // если у задачи задано время начала
                if (timeCheckerMap(inputTask)) { // проверяем, не пересекается ли она с другими задачами по времени
                    throw new IllegalArgumentException("Задача " + inputTask.getName() +" не была добавлена, т.к. пересекается с уже существующими задачами.");
                }
            }
            inputTask.setId(getNextId());
            taskMap.put(id, inputTask);
            if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
                sortedTasks.add(inputTask); // добавляем в sortedTasks
                sortTasks(); // сортируем список после добавления по startTime
            }
            return inputTask;
        } catch (IllegalArgumentException ex) {
            System.out.println("Ошибка: " + ex.getMessage()); // Обрабатываем исключение
            return null;
        }
    }

    @Override
    public Epic createEpic(Epic inputEpic) {
        inputEpic.setId(getNextId());
        epicMap.put(id, inputEpic);
        if (inputEpic.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputEpic);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        return inputEpic;
    }

    @Override
    public Subtask createSubtask(Subtask inputSubtask) { // создание Subtask
        if (inputSubtask.getStartTime() != null) { // если у подзадачи задано время начала
            if (timeCheckerMap(inputSubtask)) { // то проверяем не пересекается ли она с другими задачами по времени
                throw new IllegalArgumentException("Новая задача пересекается с уже существующими задачами.");
            }
        }
        Epic epic = epicMap.get(inputSubtask.getEpicId()); // находим Эпик в мапе (получаем id Epic к которому привязаны)
        if (epic == null) { // проверяем существует ли Эпик
            throw new IllegalArgumentException("Подзадачу не удаётся добавить, т.к. Эпик указан не верно или не существует.");
        }
        inputSubtask.setId(getNextId());
        subtaskMap.put(id, inputSubtask);
        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputSubtask); // тогда добавляем в sortedTasks
            sortTasks(); // и сортируем список после добавления по startTime
        }
        // Обновляем Эпик
        addSubtaskToEpic(inputSubtask); // записывает в Эпик номер id Subtask к которому он привязан
        updateEpicDateTime(inputSubtask.getEpicId()); // записывает в Эпик время начала и завершения
        if (epicMap.get(inputSubtask.getEpicId()).getStartTime() != null) { // проверяем, задано ли время начала у подзадачи
            if (!sortedTasks.contains(epicMap.get(inputSubtask.getEpicId()))) { // Если Эпика нет в списке, то
                sortedTasks.add(epicMap.get(inputSubtask.getEpicId()));// добавляем Эпик в sortedTasks
                sortTasks(); // и сортируем список после добавления по startTime
            }
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
                .map(subtask -> {
                    LocalDateTime startTimeSubtask = subtask.getStartTime();
                    Duration duration = subtask.getDuration();
                    if (startTimeSubtask != null && duration != null) {
                        return subtask.getEndTime(duration, startTimeSubtask);
                    }
                    return null;
                })
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
        if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.remove(inputTask);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        int oldId = inputTask.getId();
        taskMap.put(oldId, inputTask);
        if (inputTask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputTask);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        return Optional.of(inputTask);
    }

    @Override
    public Optional<Epic> updateEpic(Epic inputEpic) {
        if (epicMap.get(inputEpic.getId()) == null) { // если объекта не существует, производим выход
            return Optional.empty();
        }
        if (inputEpic.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.remove(inputEpic);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        int oldId = inputEpic.getId();
        Epic oldEpic = epicMap.get(oldId); // находим старый Эпик в мапе
        for (int subtaskId : oldEpic.getListSubtaskIds()) {
            inputEpic.addSubtaskId(subtaskId);
        }
        inputEpic.setStatus(oldEpic.getStatus()); // вносим в новый Эпик Status старого (изменяемого)
        epicMap.put(oldId, inputEpic); // Сохраняем обновлённый Эпик
        if (inputEpic.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputEpic);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        return Optional.of(inputEpic);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask inputSubtask) {
        if (subtaskMap.get(inputSubtask.getId()) == null) { // если объекта не существует, производим выход
            return Optional.empty();
        }
        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.remove(inputSubtask);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        int oldId = inputSubtask.getId();
        Subtask oldSubtask = subtaskMap.get(oldId); // находим старый Subtask в мапе
        inputSubtask.setEpicId(oldSubtask.getEpicId()); // вносим новый Subtask epicId старого (изменяемого)
        subtaskMap.put(oldId, inputSubtask);
        if (inputSubtask.getStartTime() != null) { // проверяем, задано ли время начала у объекта
            sortedTasks.add(inputSubtask);// Добавляется в sortedTasks и автоматически сортируется по startTime
            sortTasks(); // Сразу сортируем список после добавления
        }
        updateEpicStatus(oldSubtask.getEpicId()); // Обновление статуса Эпика
        return Optional.of(inputSubtask);
    }

    // Обновление статуса Эпика
    private void updateEpicStatus(int epicId) {
        Epic epic = epicMap.get(epicId);
        /*double sum = 0; // количество завершённых подзадач у Эпика
        for (Subtask subtask : listOfEpicSubtasks(epicId)) {
            Status status = subtask.getStatus(); // Вытаскиваем у Subtask его Статус
            if (status.equals(Status.DONE)) {  // Проверяем Статус, если завершён, то..
                sum = sum + 1; // прибавляем 1
            } else if (status.equals(Status.IN_PROGRESS)) { // если в "в процессе" то..
                sum = sum + 0.5; // прибавляем 0,5
            }
        }*/
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
        sortedTasks.remove(taskMap.get(id)); // удаляем Task из sortedTasks
        sortTasks(); // Сразу сортируем список после добавления
        historyManager.remove(taskMap.get(id).getId()); //id
        taskMap.remove(id);
    }

    @Override
    public void deleteEpicById(int id) { // deleteEpicById
        if (epicMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        sortedTasks.remove(epicMap.get(id)); // удаляем Эпик из sortedTasks
        sortTasks(); // Сразу сортируем список после добавления
        Epic epic = epicMap.get(id); // находим Эпик в мапе

        /*if (!epic.getListSubtaskIds().isEmpty()) { // проверяем, пуст ли список где хранятся Подзадачи
            for (Integer idSubtask : epic.getListSubtaskIds()) {
                subtaskMap.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
        }*/
        // Удаляем все подзадачи с помощью Stream API
        epic.getListSubtaskIds().stream()
                .forEach(subtaskId -> {
                    subtaskMap.remove(subtaskId); // удаляем подзадачу из mапа
                    historyManager.remove(subtaskId); // удаляем историю подзадачи
                });

        historyManager.remove(epicMap.get(id).getId()); // id
        epicMap.remove(id);
    }


    @Override
    public void deleteSubtaskById(int id) { // deleteSubtaskById
        if (subtaskMap.get(id) == null) { // если объекта не существует, производим выход
            return;
        }
        sortedTasks.remove(subtaskMap.get(id)); // удаляем Subtask из sortedTasks
        sortTasks(); // Сразу сортируем список после добавления
        int idEpic = subtaskMap.get(id).getEpicId(); // получаем id Epic к которому привязан Subtask
        Epic epic = epicMap.get(idEpic); // находим Эпик в мапе

        /*for (int i = 0; i < epic.getListSubtaskIds().size(); i++) {
            if (epic.getListSubtaskIds().get(i) == id) { // находим индекс подзадачи
                epic.getListSubtaskIds().remove(i); // удаляем id подзадачи
            }
        }*/
        // Используем стрим для удаления id подзадачи из списка подзадач Эпика
        epic.getListSubtaskIds().removeIf(subtaskId -> subtaskId == id);

        historyManager.remove(subtaskMap.get(id).getId());
        subtaskMap.remove(id); // удаляем Подзадачу
        updateEpicStatus(idEpic); // обновляем Эпик
    }

    //3. Дополнительные методы: a. Получение списка всех подзадач определённого эпика. - скорее всего.
    @Override
    /*public ArrayList<Subtask> listOfEpicSubtasks(int idEpic) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>(); // создаем список
        Epic epic = epicMap.get(idEpic); // Находим Эпик по id
        if (epic != null) { // Проверка, если такого Эпика не существует
            for (int subtaskId : epic.getListSubtaskIds()) { // проходимся по списку подзадач Эпика
                Subtask subtask = subtaskMap.get(subtaskId); // Находим Subtask
                listSubtasks.add(subtask); // добавляем subtask в список
            }
        }
        return listSubtasks;
    }*/
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
