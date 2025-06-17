package managers.file;

import java.io.File;

import exception.ManagerSaveException;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File fileToSave;

    // Из тз к 7-ому спринту. Пусть новый менеджер получает файл для автосохранения в своём конструкторе и сохраняет его в поле.
    public FileBackedTaskManager(HistoryManager historyManager, File file)  {
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

    private String toStringForSavingTasks(Task task) { // метод сохранения задачи в строку
        if (task.getClass().getSimpleName().equals("Subtask")) {
            Subtask subtask = (Subtask) task;
            return task.getId() + "," + task.getClass().getSimpleName().toUpperCase() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + subtask.getEpicId();
        } else {
            return task.getId() + "," + task.getClass().getSimpleName().toUpperCase() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();}
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave, false))) {
            writer.write('\uFEFF'); // Добавляем BOM для UTF-8
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : taskMap.values()) { // записываем Task в файл
                writer.write(toStringForSavingTasks(task));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Epic epic : epicMap.values()) { // записываем Epic в файл
                writer.write(toStringForSavingTasks(epic));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            for (Subtask subtask : subtaskMap.values()) { // записываем Subtask в файл
                writer.write(toStringForSavingTasks(subtask));
                writer.newLine(); // Добавляем перенос строки между задачами
            }
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    // Этот метод должен был после считывания файла, сортировать строки по id (так я решал проблему, обозначенную Вами
    // в комментариях ( - сабтаск считан раньше эпика или файл будет изменен вручную) ), а уже потом передавать в addFromString.
    // Но я не смог заставить его работать. Возникают какие-то ошибки при чтении данных, из-за чего сортировка не работает!!!

    /*public void readFile(File file) { // считывает файл по строкам
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        // Сортируем строки по ID
        Collections.sort(lines, (a, b) -> {
            // Убираем невидимый символ из строки для обоих элементов перед сравнением
            String[] split1 = a.split(",");
            split1[0] = split1[0].replace("\uFEFF", ""); // Убираем невидимый символ из строки

            String[] split2 = b.split(",");
            split2[0] = split2[0].replace("\uFEFF", ""); // Убираем невидимый символ из строки

            return Integer.compare(Integer.parseInt(split1[0]), Integer.parseInt(split2[0]));

            });

        // Обрабатываем отсортированные строки
        for (String line : lines) {
            addFromString(line);
        }
    }*/


    public void readFile(File file) { // считывает файл по строкам
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                addFromString(line); // Передаём строку в метод forString для обработки
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    private void addFromString(String line) { // создаёт из строки объект и добавляет в хеш-таблицу
        String[] split = line.split(",");
        split[0] = split[0].replace("\uFEFF", ""); // Убираем невидимый символ из строки
        try { // пропускаем первую строчку, так как она оглавление
            iDFromBackup(Integer.parseInt(split[0]));
        } catch (NumberFormatException e) {
            return;
        }
        Status status = Status.valueOf(split[3]); // Преобразуем строку в объект Status
        switch (split[1]) { // определяем какой тип задачи и сохраняем в соответсвующую хеш-таблицу
            case "TASK":
                Task task = new Task(split[2], split[4], Integer.parseInt(split[0]), status);  // создаём объект
                taskMap.put(task.getId(), task); // Добавляем задачу в taskMap
                break;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4], Integer.parseInt(split[0]));  // создаём объект
                epic.setStatus(Status.valueOf(split[3]));
                epicMap.put(epic.getId(), epic); // Добавляем задачу в taskMap
                break;
            case "SUBTASK":
                Subtask subtask = new Subtask(split[2], split[4], Integer.parseInt(split[0]), Status.valueOf(split[3]));
                subtask.setEpicId(Integer.parseInt(split[5]));
                subtaskMap.put(subtask.getId(), subtask); // Добавляем задачу в taskMap
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