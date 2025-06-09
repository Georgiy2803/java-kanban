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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {


    FileBackedTaskManager fileManager;
    String fileTest = "src\\main\\managers\\file\\fileToSaveTest.CSV";

    @BeforeEach
    public void init() throws IOException {
        fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File(fileTest));
    }


    @Test
    void creatFile_callMethod_loadFromFile() throws IOException { //Проверяем создаётся ли файл методом loadFromFile

        String nameFileTest = "fileToSaveTest.CSV";
        fileManager.loadFromFile(nameFileTest); // передаём имя файла в метод

        // Проверяем, что файл был создан методом loadFromFile (автоматически создаёт файл, если его нет)
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
    void addTaskToFile_readTaskFromFail() throws IOException {  // Запись задач в файл, и считывание задач из файла.
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
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
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
                System.out.println("Ошибка при копировании файла: " + e.getMessage());
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

}



