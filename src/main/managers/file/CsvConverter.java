package managers.file;

import model.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CsvConverter {
    private String noData = "";
    private FileBackedTaskManager fileTaskManager;

    public CsvConverter(FileBackedTaskManager fileTaskManager) {
        this.fileTaskManager = fileTaskManager;
    }

    // Метод для конвертации задачи в строку
    public String taskToString(Task task) { // метод сохранения задачи в строку
        return task.getId() + ";" + task.getTaskType() + ";" + task.getName() + ";"
                + task.getStatus() + ";" + task.getDescription() + ";" + noData + ";"
                + (task.getStartTime() != null ? task.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (task.getDuration() != null ? task.getDuration().toMinutes() : noData) + ";"
                + (task.getEndTime() != null ? task.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Перегрузка для Epic
    public String taskToString(Epic epic) { // метод сохранения задачи в строку
        return epic.getId() + ";" + epic.getTaskType() + ";" + epic.getName() + ";"
                + epic.getStatus() + ";" + epic.getDescription() + ";" + noData + ";"
                + (epic.getStartTime() != null ? epic.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (epic.getStartTime() != null && epic.getEndTime() != null ? Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes() : noData) + ";"
                + (epic.getEndTime() != null ? epic.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Перегрузка для Subtask
    public String taskToString(Subtask subtask) { // метод сохранения задачи в строку
        return subtask.getId() + ";" + subtask.getTaskType() + ";" + subtask.getName() + ";"
                + subtask.getStatus() + ";" + subtask.getDescription() + ";" + subtask.getEpicId() + ";"
                + (subtask.getStartTime() != null ? subtask.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (subtask.getDuration() != null ? subtask.getDuration().toMinutes() : noData) + ";"
                + (subtask.getEndTime() != null ? subtask.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    // Метод для формирования строки-заголовка
    public String headingToString() {
        return "id;type;name;status;description;epic;startTime;duration;endTime";
    }

    // Метод для восстановления объекта из строки
    public Task addFromString(String line) { // создаёт из строки объект и добавляет в хеш-таблицу
        String[] split = line.split(";", -1); // Разбиение строки на части
        try {
            fileTaskManager.updateIdCounter(Integer.parseInt(split[0])); // Загружаем ID и заодно проверяем корректность данных
        } catch (NumberFormatException e) {
            return null; // Если ошибка, пропускаем строку
        }
        // Создание базовой задачи
        Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), Status.valueOf(split[3]));
        task.setTaskType(TaskType.valueOf(split[1]));

        switch (task.getTaskType()) { // Обработка специфичных для типа данных
            case EPIC:
                Epic epic = new Epic(task.getName(), task.getDescription(), task.getId());
                epic.setStatus(task.getStatus());
                task = epic; // Приведение к типу Epic
                break;
            case SUBTASK:
                Subtask subtask = new Subtask(task.getName(), task.getDescription(), task.getId(), task.getStatus());
                subtask.setEpicId(Integer.parseInt(split[5]));
                task = subtask; // Приведение к типу Subtask
                break;
            default:
                break;
        }

        // Обработка временных меток
        if (!isNoData(split[6]) && !isNoData(split[7])) {
            task.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[6])), ZoneId.of("Europe/Moscow")));
            task.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));

            // Обработка поля endTime только для Epic
            if (task.getTaskType() == TaskType.EPIC && !isNoData(split[8])) {
                ((Epic) task).setEndTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[8])), ZoneId.of("Europe/Moscow")));
            }
        }
        return task;
    }

    // Метод для проверки на noData
    private boolean isNoData(String value) {
        return value == null || value.equals(noData) || value.trim().isEmpty();
    }
}