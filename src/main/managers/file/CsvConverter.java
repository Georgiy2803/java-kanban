package managers.file;

import model.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CsvConverter {
    private static String noData = "";

    // Метод для конвертации задачи в строку
    public static String taskToString(Task task) { // метод сохранения задачи в строку
        return task.getId() + ";" + task.getTaskType() + ";" + task.getName() + ";"
                + task.getStatus() + ";" + task.getDescription() + ";" + noData + ";"
                + (task.getStartTime() != null ? task.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (task.getDuration() != null ? task.getDuration().toMinutes() : noData) + ";"
                + (task.getEndTime() != null ? task.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Перегрузка для Epic
    public static String taskToString(Epic epic) { // метод сохранения задачи в строку
        return epic.getId() + ";" + epic.getTaskType() + ";" + epic.getName() + ";"
                + epic.getStatus() + ";" + epic.getDescription() + ";" + noData + ";"
                + (epic.getStartTime() != null ? epic.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (epic.getStartTime() != null && epic.getEndTime() != null ? Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes() : noData) + ";"
                + (epic.getEndTime() != null ? epic.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Перегрузка для Subtask
    public static String taskToString(Subtask subtask) { // метод сохранения задачи в строку
        return subtask.getId() + ";" + subtask.getTaskType() + ";" + subtask.getName() + ";"
                + subtask.getStatus() + ";" + subtask.getDescription() + ";" + subtask.getEpicId() + ";"
                + (subtask.getStartTime() != null ? subtask.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (subtask.getDuration() != null ? subtask.getDuration().toMinutes() : noData) + ";"
                + (subtask.getEndTime() != null ? subtask.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Метод для формирования строки-заголовка
    public static String getHeader() {
        return "id;type;name;status;description;epic;startTime;duration;endTime";
    }

    // Метод для восстановления объекта из строки
    public static Task convertToObject(String line) { // Преобразовывает из строки объект
        String[] split = line.split(";", -1); // Разбиение строки на части
        try {
            Integer checkConversion = Integer.parseInt(split[0]);// Если преобразование прошло успешно, то данные корректны
        } catch (NumberFormatException e) {
            return null; // Если ошибка, пропускаем строку
        }
        // Десериализация данных
        Integer id = Integer.parseInt(split[0]);
        TaskType taskType = TaskType.valueOf(split[1]); // Тип задач
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        Integer epicId = "".equals(split[5]) ? null : Integer.parseInt(split[5]);
        String StartTime = split[6];
        String duration = split[7];
        String endTime = split[8];

        Task task = lineToTask(name, description, id, status);
        switch (taskType) { // Обработка специфичных для типа данных
            case TASK:
                processingTimeForTask(task, StartTime, duration); // Обработка времени для задачи
                break;
            case EPIC:
                Epic epic = lineToEpic(name, description, id, status);
                task = epic; // Приведение к типу Epic
                processingTimeForTask(task, StartTime, duration); // Обработка времени для задачи
                processingTimeForEpic(epic, endTime); // Обработка времени для Эпик
                break;
            case SUBTASK:
                Subtask subtask = lineToSubtask(name, description, id, status, epicId);
                task = subtask; // Приведение к типу Subtask
                processingTimeForTask(task, StartTime, duration); // Обработка времени для подзадачи
                break;
            default:
                break;
        }
        return task;

    }

    public static Task lineToTask(String name,String description, Integer id, Status status) {
        Task task = new Task(name, description, id, status);
        return task;
    }

    public static Epic lineToEpic (String name,String description, Integer id, Status status) {
        Epic epic = new Epic(name, description, id);
        epic.setStatus(status);
        return epic;
    }

    public static Subtask lineToSubtask (String name,String description, Integer id, Status status, Integer epicId) {
        Subtask subtask = new Subtask(name, description, id, status);
        subtask.setEpicId(epicId);
        return subtask;
    }

    // Обработка времени для задачи и подзадачи
    public static Task processingTimeForTask(Task task, String StartTime, String duration) {
        if (!isNoData(StartTime) && !isNoData(duration)) {
            task.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(StartTime)), ZoneId.of("Europe/Moscow")));
            task.setDuration(Duration.ofMinutes(Long.parseLong(duration)));
        }
        return task;
    }

    // Обработка времени для Эпик
    public static Epic processingTimeForEpic(Epic epic, String endTime) {
        if (!isNoData(endTime)) {
            (epic).setEndTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(endTime)), ZoneId.of("Europe/Moscow")));
        }
        return epic;
    }

    // Метод для проверки на noData
    private static boolean isNoData(String value) {
        return value == null || value.equals(noData) || value.trim().isEmpty();
    }
}

