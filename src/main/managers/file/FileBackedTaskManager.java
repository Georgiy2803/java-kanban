package managers.file;

import java.io.File;

import managers.history.HistoryManager;
import managers.task.InMemoryTaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Optional;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private File saveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File file) throws IOException {
        super(historyManager);

        if (!file.exists()) { //  Проверяем, существует ли файл
            try {
                file = new File("src\\main\\managers\\file\\saveFile.CSV");
                file.createNewFile(); // Создаем файл, если его не существует
                System.out.println("Файл успешно создан классом FileBackedTaskManager.");
            } catch (IOException e) {
                System.out.println("Ошибка при создании файла.");
                e.printStackTrace();
            }
        } else {
            System.out.println("файл передан в класс FileBackedTaskManager.");
        }

        this.saveFile = file; // Присваиваем this.saveFile после создания файла
        newReadFile(saveFile);
    }

    public void save() {
        String filePath = "src\\main\\managers\\file\\saveFile.CSV";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Task task : getTaskMap().values()) { // записываем Task в файл
                String toString = task.toString();
                writer.write("\uFEFF"); // Добавляем BOM для UTF-8
                writer.write(toString);
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Epic epic : getEpicMap().values()) { // записываем Epic в файл
                String toString = epic.toString();
                writer.write("\uFEFF"); // Добавляем BOM для UTF-8
                writer.write(toString);
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Subtask subtask : getSubtaskMap().values()) { // записываем Subtask в файл
                String toString = subtask.toString();
                writer.write("\uFEFF"); // Добавляем BOM для UTF-8
                writer.write(toString);
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }


    public Task fromString(String value) { // превращает строку в объект
        String[] split = value.split(",");
        Status status = Status.valueOf(split[3]); // Преобразуем строку в объект Status
        Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), status);  // создаём объект
        return task;
    }


    public void readFile() { // считывает файл по строчно
        String filePath = "src\\main\\managers\\file\\saveFile.CSV";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addFromString(line); // Передаём строку в метод forString для обработки
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void newReadFile(File saveFile) { // считывает файл по строчно
        String filePath = saveFile.getPath();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addFromString(line); // Передаём строку в метод forString для обработки
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }


    public void addFromString(String line) { // создаёт из строки объект и добавляет в хеш-таблицу
        String[] split = line.split(",");
        split[0] = split[0].replace("\uFEFF", ""); // Убираем невидимый символ из строки
        backupNextId (Integer.parseInt(split[0]));
        Status status = Status.valueOf(split[3]); // Преобразуем строку в объект Status

        switch (split[1]) { // определяем какой тип задачи и сохраняем в соответсвующую хеш-таблицу
            case "Task":
                Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), status);  // создаём объект
                getTaskMap().put(task.getId(), task); // Добавляем задачу в taskMap
                break;
            case "Epic":
                Epic epic = new Epic(split[2], split[4], Integer.parseInt(split[0]), status);  // создаём объект
                getEpicMap().put(epic.getId(), epic); // Добавляем задачу в taskMap
                break;
            case "Subtask":
                Subtask subtask = new Subtask(split[2], split[4], Integer.parseInt(split[0]), status, Integer.parseInt(split[5]));  // создаём объект
                getSubtaskMap().put(subtask.getId(), subtask); // Добавляем задачу в taskMap
                break;
        }
    }


    // задаёт начальный номер id полученный из файла
    public void backupNextId (int id) {
        if (id > getId()) {
            setId(id);
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
    public Epic createEpic (Epic inputEpic) {
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
    public void deleteEpicById (int id) {
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
    public Optional<Task> updateTask (Task inputTask) {
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
    public Optional<Subtask> updateSubtask (Subtask inputSubtask) {
        super.updateSubtask(inputSubtask);
        save();
        return Optional.of(inputSubtask);
    }


}