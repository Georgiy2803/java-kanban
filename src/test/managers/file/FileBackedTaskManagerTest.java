package managers.file;

import managers.history.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {


    FileBackedTaskManager fileManager;
    String fileTest = "src\\main\\managers\\file\\fileToSaveTest.CSV";

    @BeforeEach
    public void init() throws IOException {
        fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File(fileTest));
    }

    @Test
    void creatFile_callMethod_loadFromFile() { //Проверяем создаётся ли файл методом loadFromFile
        String nameFileTest = "fileToSaveTest.CSV";

        fileManager.loadFromFile(nameFileTest); // передаём имя файла в метод

        // Проверяем, что файл был создан методом loadFromFile
        File file = new File(fileTest);
        if (file.isFile()) {
            System.out.println("Файл обнаружен.");
        } else {
            System.out.println("Файл Не обнаружен.");
        }

        assertEquals(true, file.isFile(), "Файл не был создан методом loadFromFile");

        // удаляем файл по окончанию теста
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }
    }


    @Test
    void addTaskToFile_readTaskFromFail()  {  // Запись задач в файл, и считывание задач из файла.
        // Имитируем первый запуск приложения
        fileManager.deleteAllTasks();
        fileManager.deleteAllEpic();
        fileManager.deleteAllSubtask();

        // Убеждаемся, что хеш-таблицы с "задачами" пустые
        assertEquals(0, fileManager.getTasks().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getEpics().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getSubtask().size(), "Список задач не пуст");

        // Создаём "задачи"
        Task task1 = fileManager.createTask(new Task("Задача 1", "Описание 1"));
        Task task2 = fileManager.createTask(new Task("Задача 2", "Описание 2"));

        Epic epic1 = fileManager.createEpic(new Epic("Эпик 1", "Описание - 3 подзадачи"));
        Epic epic2 = fileManager.createEpic(new Epic("Эпик 2", "Описание - без подзадач"));

        Subtask subtask1 = fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        Subtask subtask2 = fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        Subtask subtask3 = fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));

        // Проверяем, что "задачи" появились в хеш-таблицах
        assertEquals(2, fileManager.getTasks().size(), "Хеш-таблица задач не соответствует размеру");
        assertEquals(2, fileManager.getEpics().size(), "Хеш-таблица эпиков не соответствует размеру");
        assertEquals(3, fileManager.getSubtask().size(), "Хеш-таблица подзадач не соответствует размеру");

        // Проверяем, что "задачи" есть в файле
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileTest))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: ", e);
        }

        assertEquals(8, lineCount, "Задачи или отсутствуют в файле или их количество не совпадает");


        // Создаём копию файла
        String copyFileTest = "src\\main\\managers\\file\\copyFileToSaveTest.CSV";
        Path sourcePath = Paths.get(fileTest);
        Path targetPath = Paths.get(copyFileTest);

        try {
            Files.copy(sourcePath, targetPath);
            System.out.println("Файл успешно скопирован.");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при копировании файла: ", e);
        }

        // Удаляем "задачи" в хеш-таблицах, имитируя перезагрузку приложения
        fileManager.deleteAllTasks();
        fileManager.deleteAllEpic();
        fileManager.deleteAllSubtask();

        assertEquals(0, fileManager.getTasks().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getEpics().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getSubtask().size(), "Список задач не пуст");

        // Загружаем "задачи" из файла в хеш-таблицы
        File copyFile = new File(copyFileTest);
        fileManager.readFile(copyFile);

        // Проверяем, что "задачи" появились в хеш-таблицах после загрузки из файла
        assertEquals(2, fileManager.getTasks().size(), "Хеш-таблица задач не соответствует размеру");
        assertEquals(2, fileManager.getEpics().size(), "Хеш-таблица эпиков не соответствует размеру");
        assertEquals(3, fileManager.getSubtask().size(), "Хеш-таблица подзадач не соответствует размеру");

        // Проверяем, что у Эпика отобразились его подзадачи
        //System.out.println(fileManager.getEpicById(3).get().getListSubtaskIds());
        assertEquals("[5, 6, 7]", fileManager.getEpicById(3).get().getListSubtaskIds().toString(), "Подзадачи не соответствуют");
        assertEquals("[]", fileManager.getEpicById(4).get().getListSubtaskIds().toString(), "Подзадачи не соответствуют");

        // Проверяем, после загрузки, у новых созданных задач id не пересекаются
        Task task3 = fileManager.createTask(new Task("Задача 3", "Описание 3"));
        assertEquals(8, task3.getId(), "id задачи неверный");

        // удаляем файлы по окончанию теста
        File originalFile = new File(fileTest);
        boolean deletedOriginalFile = originalFile.delete();
        if (deletedOriginalFile) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }
        boolean deletedCopyFile = copyFile.delete();
        if (deletedCopyFile) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }
    }

    @Test
    void addTaskOverTimeToFile_readTaskFromFail() {  // Запись задач в файл, и считывание задач из файла.
        // Создаём "задачи"
        fileManager.createTask(new Task("Task1", "", LocalDateTime.of(2025, 6, 21, 11, 1), Duration.ofMinutes(58)));
        fileManager.createTask(new Task("Task2", "", LocalDateTime.of(2025, 6, 21, 13, 0), Duration.ofMinutes(20)));
        fileManager.createTask(new Task("Task3", "", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(20)));
        fileManager.createTask(new Task("Task4", "", LocalDateTime.of(2025, 6, 21, 12, 21), Duration.ofMinutes(38)));
        fileManager.createTask(new Task("Task5", "", LocalDateTime.of(2025, 6, 21, 10, 0), Duration.ofMinutes(60)));
        fileManager.createTask(new Task("Task6", "Описание 1")); // создаём задачу без времени
        fileManager.createTask(new Task("Task7", "Описание 2")); // создаём задачу без времени

        fileManager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        fileManager.createEpic(new Epic("Эпик 2", "1 подзадача"));
        fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 15, 1), Duration.ofMinutes(55)));
        fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 16, 6), Duration.ofMinutes(20)));
        fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 17, 0), Duration.ofMinutes(5)));
        fileManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 9));

        // проверка количества Task в списке (HashMap)
        assertEquals(7, fileManager.getTasks().size(), "В списке хеш-таблицы не верное количество Task");
        // проверка количества Эпиков в списке (HashMap)
        assertEquals(2, fileManager.getEpics().size(), "В списке хеш-таблицы не верное количество Эпиков");
        // проверка количества Subtask в списке (HashMap)
        assertEquals(4, fileManager.getSubtask().size(), "В списке хеш-таблицы не верное количество Subtask");

        // количество задач в отсортированном списке по приоритету (startTime)
        assertEquals(9, fileManager.getPrioritizedTasks().size(), "В списке отсортирванные задачи по приоритету (startTime) не верное количество задач");

        // Проверяем, что "задачи" есть в файле
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileTest))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла: ", e);
        }

        assertEquals(14, lineCount, "Задачи или отсутствуют в файле или их количество не совпадает");


        // создаём новый объект и проверяем, что в него загрузились задачи из файла.
        FileBackedTaskManager newFileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File(fileTest));

        // Проверяем, что "задачи" появились в хеш-таблицах после загрузки из файла
        assertEquals(7, newFileManager.getTasks().size(), "Хеш-таблица задач не соответствует размеру");
        assertEquals(2, newFileManager.getEpics().size(), "Хеш-таблица эпиков не соответствует размеру");
        assertEquals(4, newFileManager.getSubtask().size(), "Хеш-таблица подзадач не соответствует размеру");

        // Проверяем, что у Эпика отобразились его подзадачи
        //System.out.println(fileManager.getEpicById(3).get().getListSubtaskIds());
        assertEquals("[10, 11, 12]", newFileManager.getEpicById(8).get().getListSubtaskIds().toString(), "Подзадачи не соответствуют");
        assertEquals("[13]", newFileManager.getEpicById(9).get().getListSubtaskIds().toString(), "Подзадачи не соответствуют");

        // Проверяем, после загрузки, у новых созданных задач id не пересекаются
        Task task8 = newFileManager.createTask(new Task("Задача 8", "Описание 3"));
        assertEquals(14, task8.getId(), "id задачи неверный");

        // удаляем файлы по окончанию теста
       File originalFile = new File(fileTest);
        boolean deletedOriginalFile = originalFile.delete();
        if (deletedOriginalFile) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }
    }
}



