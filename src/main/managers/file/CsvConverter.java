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

    public static Task stringToTask(String line){
        String[] parts = line.split(";",-1);
        TaskType type = TaskType.valueOf(parts[1]);
        switch(type) {
            case TASK: return buildTask(parts);
            case SUBTASK: return buildSubtask(parts);
            case EPIC: return buildEpic(parts);
            default: return null;
        }
    }

    private static Task buildTask(String[] parts) {
        Task task = new Task(parts[2], parts[4], Integer.parseInt(parts[0]), Status.valueOf(parts[3]));
        processingTimeForTask(task, parts[6], parts[7]); // Обработка времени для задачи
        return task;
    }


    private static Epic buildEpic(String[] parts) {
        Epic epic = new Epic(parts[2], parts[4], Integer.parseInt(parts[0]));
        epic.setStatus(Status.valueOf(parts[3]));
        processingTimeForTask(epic, parts[6], parts[7]); // Обработка времени для задачи
        processingTimeForEpic(epic, parts[8]); // Обработка времени для Эпик
        return epic;
    }

    private static Subtask buildSubtask(String[] parts) {
        Subtask subtask = new Subtask(parts[2], parts[4], Integer.parseInt(parts[0]), Status.valueOf(parts[3]));
        subtask.setEpicId(Integer.parseInt(parts[5]));
        processingTimeForTask(subtask, parts[6], parts[7]); // Обработка времени для подзадачи
        return subtask;
    }

    // Обработка времени для задачи и подзадачи
    public static Task processingTimeForTask(Task task, String StartTime, String duration) {
        task.setStartTime(stringToDate(StartTime));
        task.setDuration(stringToDuration(duration));
        return task;
    }

    // Обработка времени для Эпик
    public static Epic processingTimeForEpic(Epic epic, String endTime) {
        epic.setEndTime(stringToDate(endTime));
        return epic;
    }

    // конвертирует из строки - дату "начала задачи"/"конца задачи"
    public static LocalDateTime stringToDate
    (String line) {
        if (isNoData(line)) {
            return null;
        } else {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(line)), ZoneId.of("Europe/Moscow"));
        }
    }

    // конвертирует из строки - "продолжительность задачи"
    public static Duration stringToDuration(String line) {
        if (isNoData(line)) {
            return null;
        } else {
            return Duration.ofMinutes(Long.parseLong(line));
        }
    }

    // Метод проверяет, является ли строка пустой, null или значение noData
    private static boolean isNoData(String value) {
        return value == null || value.equals(noData) || value.trim().isEmpty();
    }
}

