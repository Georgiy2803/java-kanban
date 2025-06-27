package managers.file;

import java.io.File;

import exception.ManagerSaveException;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import model.*;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static model.TaskType.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;
    private String noData = "н/д";

    // Из тз к 7-ому спринту. Пусть новый менеджер получает файл для автосохранения в своём конструкторе и сохраняет его в поле.
    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);

        if (!file.exists()) { //  Проверяем, существует ли файл
            try {
                file = new File(file.getPath());
                file.createNewFile(); // Создаем файл, если его не существует
                System.out.println("Файл успешно создан классом FileBackedTaskManager.");
            } catch (IOException e) {
                throw new RuntimeException("Ошибка при создании файла.", e);
            }
        } else {
            System.out.println("файл передан в класс FileBackedTaskManager.");
        }

        this.fileToSave = file; // Присваиваем this.saveFile после создания файла
        readFile(fileToSave);
    }

    /* Из тз к 7-ому спринту.
    Создайте статический метод static FileBackedTaskManager loadFromFile(File file), который будет восстанавливать
     данные менеджера из файла при запуске программы. */
    // Метод принимает имя файла, если такого файла не существует, то создаёт его.
    public static FileBackedTaskManager loadFromFile(String fileName) {
        File file = new File("src\\main\\managers\\file\\" + fileName);
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        if (!file.exists()) {  // Проверяем, существует ли файл
            try {
                file.createNewFile(); // Создаём файл, если его нет
                System.out.println("файл создан методом loadFromFile.");
            } catch (IOException e) {
                // Обрабатываем исключение здесь
                throw new RuntimeException("Ошибка при создании файла: " + e.getMessage(), e);
            }
        } else {
            System.out.println("файл передан в метод loadFromFile.");
        }

        fileBackedTaskManager.readFile(file);
        return fileBackedTaskManager;
    }

    private String toStringForSavingTask(Task task) { // метод сохранения задачи в строку
        return task.getId() + ";" + task.getTaskType() + ";" + task.getName() + ";"
                + task.getStatus() + ";" + task.getDescription() + ";" + noData + ";"
                + (task.getStartTime() != null ? task.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (task.getDuration() != null ? task.getDuration().toMinutes() : noData) + ";"
                + (task.getEndTime() != null ? task.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    private String toStringForSavingEpic(Epic epic) { // метод сохранения Эпик в строку
        return epic.getId() + ";" + epic.getTaskType() + ";" + epic.getName() + ";"
                + epic.getStatus() + ";" + epic.getDescription() + ";" + noData + ";"
                + (epic.getStartTime() != null ? epic.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (epic.getStartTime() != null && epic.getEndTime() != null ? Duration.between(epic.getStartTime(), epic.getEndTime()).toMinutes() : noData) + ";"
                + (epic.getEndTime() != null ? epic.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    private String toStringForSavingSubtask(Subtask subtask) { // метод сохранения подзадачи в строку
        return subtask.getId() + ";" + subtask.getTaskType() + ";" + subtask.getName() + ";"
                + subtask.getStatus() + ";" + subtask.getDescription() + ";" + subtask.getEpicId() + ";"
                + (subtask.getStartTime() != null ? subtask.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData) + ";"
                + (subtask.getDuration() != null ? subtask.getDuration().toMinutes() : noData) + ";"
                + (subtask.getEndTime() != null ? subtask.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond() : noData);
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave, false))) {
            writer.write('\uFEFF'); // Добавляем BOM для UTF-8
            writer.write("id;type;name;status;description;epic;startTime;duration;endTime");
            writer.newLine();
            for (Task task : taskMap.values()) { // записываем Task в файл
                writer.write(toStringForSavingTask(task));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Epic epic : epicMap.values()) { // записываем Epic в файл
                writer.write(toStringForSavingEpic(epic));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Subtask subtask : subtaskMap.values()) { // записываем Subtask в файл
                writer.write(toStringForSavingSubtask(subtask));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    public void readFile(File file) { // считывает файл по строкам
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Просто прочитаем первую строку и проигнорируем её (заголовочная строка)
            while ((line = reader.readLine()) != null) { // Дальше читаем остальные строки
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        // Сортируем строки по ID
        Collections.sort(lines, (a, b) -> {
            String[] split1 = a.split(";");
            String[] split2 = b.split(";");

            return Integer.compare(Integer.parseInt(split1[0]), Integer.parseInt(split2[0]));
        });

        for (String line : lines) { // Передаём отсортированные строки
            addFromString(line);
        }
    }


    // метод для проверки на noData
    private boolean isNoData(String value) {
        return value == null || value.equals(noData) || value.trim().isEmpty();
    }

    // Теперь используем этот метод в методе addFromString
    private void addFromString(String line) { // создаёт из строки объект и добавляет в хеш-таблицу
        String[] split = line.split(";");
        try { // пропускаем строчку, если в ней ошибка
            iDFromBackup(Integer.parseInt(split[0]));
        } catch (NumberFormatException e) {
            return;
        }
        switch (split[1]) { // определяем какой тип задачи и сохраняем в соответсвующую хеш-таблицу
            case "TASK":
                Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), Status.valueOf(split[3]));  // создаём обычный объект
                // проверяем, что временные метки есть в файле
                if (!isNoData(split[6]) && !isNoData(split[7])) {
                    task.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[6])), ZoneId.of("Europe/Moscow")));
                    task.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));
                    task.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[8])), ZoneId.of("Europe/Moscow")));
                }
                taskMap.put(task.getId(), task); // Добавляем задачу в taskMap
                break;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4], Integer.parseInt(split[0]));  // создаём объект
                epic.setStatus(Status.valueOf(split[3]));
                if (!isNoData(split[6]) && !isNoData(split[8])) {
                    epic.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[6])), ZoneId.of("Europe/Moscow")));
                    epic.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));
                    epic.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[8])), ZoneId.of("Europe/Moscow")));
                }
                epicMap.put(epic.getId(), epic); // Добавляем задачу в epicMap
                break;
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4], Integer.parseInt(split[0]), Status.valueOf(split[3]));
                subtask.setEpicId(Integer.parseInt(split[5]));
                if (!isNoData(split[6]) && !isNoData(split[7])) {
                    subtask.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[6])), ZoneId.of("Europe/Moscow")));
                    subtask.setDuration(Duration.ofMinutes(Long.parseLong(split[7])));
                    subtask.setEndTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(split[8])), ZoneId.of("Europe/Moscow")));
                }
                subtaskMap.put(subtask.getId(), subtask); // Добавляем задачу в subtaskMap
                Epic epicThisSubtask = epicMap.get(Integer.parseInt(split[5])); // Находим Эпик по id
                if (epicThisSubtask != null) { // Проверка, если такого Эпика не существует
                    epicThisSubtask.addSubtaskId(Integer.parseInt(split[0])); // добавляем id Subtask в Список Эпика
                }
                break;
            default: // если ни одно условие не подошло
                break;
        }
    }

    // задаёт начальный номер id полученный из файла
    private void iDFromBackup(int newId) {
        if (newId > id) {
            id = newId;
        }
    }

    // создание объектов
    @Override
    public Task createTask(Task inputTask) {
        super.createTask(inputTask);
        save();
        return inputTask;
    }

    @Override
    public Epic createEpic(Epic inputEpic) {
        super.createEpic(inputEpic);
        save();
        return inputEpic;
    }

    @Override
    public Subtask createSubtask(Subtask inputSubtask) { // создание Subtask
        super.createSubtask(inputSubtask);
        save();
        return inputSubtask;
    }


    // Удаление объектов
    @Override
    public void deleteAllTasks() { // удаление всех Task
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpic() { // удаление всех Epic и связанных с ними Subtask
        super.deleteAllEpic();
        save();
    }

    @Override
    public void deleteAllSubtask() { // Удаление всех Subtask. Очистка списков у Эпиков и обновление их статуса
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    // 2e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public Optional<Task> updateTask(Task inputTask) {
        super.updateTask(inputTask);
        save();
        return Optional.of(inputTask);
    }

    @Override
    public Optional<Epic> updateEpic(Epic inputEpic) {
        super.updateEpic(inputEpic);
        save();
        return Optional.of(inputEpic);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask inputSubtask) {
        super.updateSubtask(inputSubtask);
        save();
        return Optional.of(inputSubtask);
    }
}