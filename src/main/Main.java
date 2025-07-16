
import managers.Managers;
import managers.file.FileBackedTaskManager;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Main {
    public static void main(String[] args) throws IOException {

       /*TaskManager manager = Managers.getDefault();

        //manager.createTask(new Task("Task 1", "Описание 1"));
        manager.createTask(new Task("Task 2", "Описание второй задачи", LocalDateTime.now(), Duration.ofMinutes(120)));
        manager.createTask(new Task("Task 2", "Описание второй задачи", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(120)));
        manager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        manager.createEpic(new Epic("Эпик 2", "без подзадач"));

        manager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(120)));
        manager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 13, 0), Duration.ofMinutes(180)));
        manager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 11, 0), Duration.ofMinutes(60)));
        manager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println();
        System.out.println("Приоритетный список");
        System.out.println(manager.getPrioritizedTasks());
       */


/*
        manager.createTask(new Task("Task 1", "Описание 1"));
        manager.createTask(new Task("Task 2", "Описание 2"));
        manager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        manager.createEpic(new Epic("Эпик 2", "без подзадач"));

        manager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));
        System.out.println("1 вывод");

        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(6);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getSubtaskById(5);
        manager.getSubtaskById(7);
        manager.getSubtaskById(6);
        manager.getSubtaskById(8);
        manager.getTaskById(1);
        manager.getTaskById(2);

        System.out.println(manager.getTaskById(1).get());
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());



        System.out.println("Последние просмотренные задачи: \n" + manager.getHistory() + "\n");



        System.out.println("Конец \n");


        manager.updateTask(new Task("Купить продукты", "Молоко, хлеб, помидоры", 1, Status.DONE));
        manager.updateTask(new Task("Провести тренировку", "Подтягивание, отжимание, приседания", 2, Status.IN_PROGRESS));
        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("2 вывод");

        System.out.println(manager.getTasks());

        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("Конец \n");



        manager.updateSubtask(new Subtask("Купить коробки1", "Размер 350х350х300", 5, Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Купить скотч1", "5 шт. - прозрачного", 6, Status.DONE));
        manager.updateSubtask(new Subtask("Составить план1", "Начертить схему", 7, Status.DONE));

        manager.deleteEpicById(3);
        System.out.println("3 вывод");
        manager.updateEpic(new Epic("Переезд1", "В другой офис1", 3));


        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");


        System.out.println(manager.getEpics().get(0).getId());


        System.out.println("4 вывод");

        manager.deleteAllTasks();
        manager.deleteAllEpic();
        manager.deleteAllSubtask();


        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());


        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("Конец \n");*/









        InMemoryTaskManager InMemory = new InMemoryTaskManager(new InMemoryHistoryManager());

//        InMemory.createTask(new Task("Task", "", LocalDateTime.of(2025, 6, 21, 10, 0), Duration.ofMinutes(60)));
//        InMemory.createTask(new Task("Task", "", LocalDateTime.of(2025, 6, 21, 11, 1), Duration.ofMinutes(55)));
//        InMemory.createTask(new Task("Task", "", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(20)));
//        InMemory.createTask(new Task("Task", "", LocalDateTime.of(2025, 6, 21, 13, 0), Duration.ofMinutes(59)));
//
//        Task task1 = new Task("Task 1", "", LocalDateTime.of(2025, 6, 21, 9, 0), Duration.ofMinutes(9));
//        Task task2 = new Task("Task 2", "", LocalDateTime.of(2025, 6, 21, 12, 21), Duration.ofMinutes(38));
//        Task task3 = new Task("Task 3", "", LocalDateTime.of(2025, 6, 21, 14, 00), Duration.ofMinutes(60));
//
//        InMemory.createTask(task1);
//        InMemory.createTask(task2);
//        InMemory.createTask(task3);
//
//        InMemory.createEpic(new Epic("Эпик 1", "3 подзадачи"));
//        InMemory.createEpic(new Epic("Эпик 2", "без подзадач"));



        InMemory.createTask(new Task("Task1", "", LocalDateTime.of(2025, 6, 21, 11, 1), Duration.ofMinutes(58)));
        InMemory.createTask(new Task("Task2", "", LocalDateTime.of(2025, 6, 21, 9, 0), Duration.ofMinutes(20)));
        // System.out.println(InMemory.getTaskById(2));
        InMemory.createTask(new Task("Task3", "", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(20)));
        InMemory.createTask(new Task("Task4", "", LocalDateTime.of(2025, 6, 21, 12, 21), Duration.ofMinutes(38)));
        InMemory.createTask(new Task("Task5", "", LocalDateTime.of(2025, 6, 21, 10, 0), Duration.ofMinutes(60)));
        InMemory.createTask(new Task("Task6", "Описание 1")); // создаём задачу без времени
        InMemory.createTask(new Task("Task7", "Описание 2")); // создаём задачу без времени

        InMemory.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        InMemory.createEpic(new Epic("Эпик 2", "1 подзадача"));

        InMemory.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 15, 1), Duration.ofMinutes(55)));
        InMemory.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 16, 6), Duration.ofMinutes(20)));
        InMemory.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 17, 0), Duration.ofMinutes(5)));
        InMemory.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 9));


        //  System.out.println(InMemory.getPrioritizedTasks());

        //System.out.println(InMemory.getPrioritizedTasks());
        //*System.out.println(InMemory.getTasks());
        //System.out.println(InMemory.getEpics());
        //  System.out.println(InMemory.getSubtask());



        // InMemory.updateTask(new Task("Task1", "обновил", 1, Status.DONE));
        InMemory.updateTask(new Task("Task2", "обновил", 2, Status.DONE, LocalDateTime.of(2025, 6, 21, 11, 0), Duration.ofMinutes(20)));
        //System.out.println(InMemory.getTaskById(2));
        InMemory.updateEpic(new Epic("Эпик 1", "Новый 3 подзадачи",8));

        InMemory.updateSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 10, Status.DONE, LocalDateTime.of(2024, 6, 21, 15, 1), Duration.ofMinutes(55)));
        InMemory.updateSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 11, Status.DONE, LocalDateTime.of(2024, 6, 21, 17, 6), Duration.ofMinutes(20)));
        InMemory.updateSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 12, Status.DONE, LocalDateTime.of(2024, 6, 21, 17, 0), Duration.ofMinutes(5)));

// Subtask(String name, String description, Integer id, Status status)
        InMemory.updateSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 13, Status.DONE));

        //  InMemory.deleteTaskById(3);
        // InMemory.deleteEpicById(9);
        // InMemory.deleteSubtaskById(12);

        //  InMemory.deleteAllTasks();
        //  InMemory.deleteAllEpic();
        // InMemory.deleteAllSubtask();

        System.out.println(InMemory.getPrioritizedTasks());


        System.out.println(InMemory.getTasks());
        System.out.println(InMemory.getEpics());
        System.out.println(InMemory.getSubtask());




        /*FileBackedTaskManager fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("src\\main\\managers\\file\\fileToSaveTest.CSV"));


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
        fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 16, 6), Duration.ofMinutes(200)));
        fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 8, LocalDateTime.of(2025, 6, 21, 16, 0), Duration.ofMinutes(5)));
        fileManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 9));*/


        /*fileManager.createTask(new Task("Task 2", "Описание 2"));
        fileManager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        fileManager.createEpic(new Epic("Эпик 2", "без подзадач"));

        fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));

        fileManager.updateSubtask(new Subtask("Купить коробки1", "Размер 350х350х300", 5, Status.IN_PROGRESS));
        fileManager.updateSubtask(new Subtask("Купить скотч1", "5 шт. - прозрачного", 6, Status.DONE));
        fileManager.updateSubtask(new Subtask("Составить план1", "Начертить схему", 7, Status.DONE));*/

        /*fileManager.createTask(new Task("Task 2", "Описание второй задачи", LocalDateTime.now(), Duration.ofMinutes(120)));
        fileManager.createTask(new Task("Task 2", "Описание второй задачи", LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(120)));
        fileManager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        fileManager.createEpic(new Epic("Эпик 2", "без подзадач"));

        fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 12, 0), Duration.ofMinutes(120)));
        fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 13, 0), Duration.ofMinutes(180)));
        fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3, LocalDateTime.of(2025, 6, 21, 11, 0), Duration.ofMinutes(60)));
        fileManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));*/



        /*System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());
        System.out.println("Приоритетный список");
        System.out.println(fileManager.getPrioritizedTasks());

        String fileTest = "src\\main\\managers\\file\\fileToSaveTest.CSV";

        // Создаём копию файла
        String copyFileTest = "src\\main\\managers\\file\\fileToSaveTest2.CSV";
        Path sourcePath = Paths.get(fileTest);
        Path targetPath = Paths.get(copyFileTest);

        try {
            Files.copy(sourcePath, targetPath);
            System.out.println("Файл успешно скопирован.");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при копировании файла: ", e);
        }

        fileManager.deleteAllTasks();
        fileManager.deleteAllEpic();
        fileManager.deleteAllSubtask();
        System.out.println("Очистка \n");

        // Загружаем "задачи" из файла в хеш-таблицы
        File copyFile = new File(copyFileTest);
        fileManager.readFile(copyFile);

        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());
        System.out.println("Приоритетный список");
        System.out.println(fileManager.getPrioritizedTasks());*/

    }
}