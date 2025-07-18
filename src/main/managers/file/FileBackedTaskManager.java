package managers.file;

import java.io.File;

import exception.IntersectionException;
import exception.ManagerSaveException;
import exception.NotFoundException;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import model.*;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;

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

    // Метод восстанавливает данные менеджера из файла при запуске программы.
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

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave, false))) {
            writer.write('\uFEFF');
            writer.write(CsvConverter.getHeader());
            writer.newLine();
            for (Task task : taskMap.values()) {
                writer.write(CsvConverter.taskToString(task));
                writer.newLine();
            }
            for (Epic epic : epicMap.values()) {
                writer.write(CsvConverter.taskToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtaskMap.values()) {
                writer.write(CsvConverter.taskToString(subtask));
                writer.newLine();
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    private void readFile(File file) { // считывает файл по строкам конвертирует и добавляет его в мар
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Читаем и игнорируем первую строку (заголовочную)

            String line;
            while ((line = reader.readLine()) != null) {
                Task task = CsvConverter.stringToTask(line); // Преобразуем строку в объект
                updateIdCounter(task.getId()); // задаём начальный номер id полученный из файла
                addMap(task); // Добавляем задачу в мар
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        // Связываем подзадачи с их эпиками
        calcualteData();
    }

    private void addMap (Task task) { // добавляем задачу в соответсвующий Мар
        switch (task.getTaskType()) {
            case TASK:
                taskMap.put(task.getId(), task);
                if (task.getStartTime() != null) { // проверяем, задано ли время начала у объекта
                    sortedTasks.add(task); // добавляем в sortedTasks
                }
                break;
            case EPIC:
                epicMap.put(task.getId(), (Epic) task);
                if (task.getStartTime() != null) { // проверяем, задано ли время начала у объекта
                    sortedTasks.add(task); // добавляем в sortedTasks
                }
                break;
            case SUBTASK:
                subtaskMap.put(task.getId(), (Subtask) task);
                if (task.getStartTime() != null) { // проверяем, задано ли время начала у объекта
                    sortedTasks.add(task); // добавляем в sortedTasks
                }
                break;
            default:
                break;
        }
    }

    // Связываем подзадачи с их эпиками
    private void calcualteData() {
        for (Subtask subtask : subtaskMap.values()) {
            Epic epic = epicMap.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtaskId(subtask.getId()); // Добавляем id подзадачи в список связанных подзадач
            }
        }
    }

    // задаёт начальный номер id полученный из файла
    private void updateIdCounter(int newId) {
        if (newId > id) {
            id = newId;
        }
    }

    // создание объектов
    @Override
    public Task createTask(Task inputTask) throws IntersectionException {
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
    public Subtask createSubtask(Subtask inputSubtask) throws IntersectionException { // создание Subtask
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
    public Optional<Task> updateTask(Task inputTask) throws IntersectionException, NotFoundException {
        super.updateTask(inputTask);
        save();
        return Optional.of(inputTask);
    }

    @Override
    public Optional<Epic> updateEpic(Epic inputEpic) throws NotFoundException {
        super.updateEpic(inputEpic);
        save();
        return Optional.of(inputEpic);
    }

    @Override
    public Optional<Subtask> updateSubtask(Subtask inputSubtask) throws IntersectionException, NotFoundException {
        super.updateSubtask(inputSubtask);
        save();
        return Optional.of(inputSubtask);
    }
}