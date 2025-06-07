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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {


    FileBackedTaskManager fileManager;

    @BeforeEach
    public void init() throws IOException {
        fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("src\\main\\managers\\file\\saveFile.CSV"));
    }


    @Test
    void creatFile_callСlass_FileBackedTaskManager() { //Проверяем создаётся ли файл классом FileBackedTaskManager

        // Проверяем, что файл был создан классом FileBackedTaskManager (автоматически создаёт файл, если его нет)
        String filePath = "src\\main\\managers\\file\\saveFile.CSV";
        File file = new File(filePath);
        if (file.isFile()) {
            System.out.println("Файл обнаружен.");
        } else {
            System.out.println("Файл Не обнаружен.");
        }

        assertEquals(true, file.isFile(), "Файл не был создан классом FileBackedTaskManager");

        // удаляем файл по окончанию теста
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }
    }

    @Test
    void addTaskToFile_readTaskFromFail() {  // Запись задач в файл, и считывание задач из файла.
        // Имитируем первый запуск приложения
        fileManager.getTaskMap().clear();
        fileManager.getEpicMap().clear();
        fileManager.getSubtaskMap().clear();
        fileManager.setId(0);

        // Убеждаемся, что хеш-таблицы с "задачами" пустые
        assertEquals(0, fileManager.getTasks().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getEpicMap().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getSubtask().size(), "Список задач не пуст");

        // Создаём "задачи"
        Task task1 = fileManager.createTask(new Task("Задача 1564", "Описание 1"));
        Task task2 = fileManager.createTask(new Task("Задача 2", "Описание 2"));

        Epic epic1 = fileManager.createEpic(new Epic("Эпик 1", "Описание - 3 подзадачи"));
        Epic epic2 = fileManager.createEpic(new Epic("Эпик 2", "Описание - без подзадач"));

        Subtask subtask1 = fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        Subtask subtask2 = fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        Subtask subtask3 = fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));

        // Проверяем, что "задачи" появились в хеш-таблицах
        assertEquals(2, fileManager.getTasks().size(), "Хеш-таблица задач не соответствует размеру");
        assertEquals(2, fileManager.getEpicMap().size(), "Хеш-таблица эпиков не соответствует размеру");
        assertEquals(3, fileManager.getSubtask().size(), "Хеш-таблица подзадач не соответствует размеру");

        // Проверяем, что "задачи" есть в файле
        String filePath = "src\\main\\managers\\file\\saveFile.CSV";
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }

        assertEquals(7, lineCount, "Задачи или отсутствуют в файле или их количество не совпадает");

        // Удаляем "задачи" в хеш-таблицах, имитируя перезагрузку приложения
        fileManager.getTaskMap().clear();
        fileManager.getEpicMap().clear();
        fileManager.getSubtaskMap().clear();

        assertEquals(0, fileManager.getTasks().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getEpicMap().size(), "Список задач не пуст");
        assertEquals(0, fileManager.getSubtask().size(), "Список задач не пуст");

        // Загружаем "задачи" из файла в хеш-таблицы
        File file = new File(filePath);
        fileManager.newReadFile(file);

        // Проверяем, что "задачи" появились в хеш-таблицах после загрузки из файла
        assertEquals(2, fileManager.getTasks().size(), "Хеш-таблица задач не соответствует размеру");
        assertEquals(2, fileManager.getEpicMap().size(), "Хеш-таблица эпиков не соответствует размеру");
        assertEquals(3, fileManager.getSubtask().size(), "Хеш-таблица подзадач не соответствует размеру");

        // удаляем файл по окончанию теста
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("Файл успешно удалён.");
        } else {
            System.out.println("Ошибка при удалении файла.");
        }

    }

}



