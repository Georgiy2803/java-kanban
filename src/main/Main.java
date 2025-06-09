
import managers.Managers;
import managers.file.FileBackedTaskManager;
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


public class Main {
    public static void main(String[] args) throws IOException {

        /*TaskManager manager = Managers.getDefault();



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
        System.out.println("Конец \n");



        FileBackedTaskManager fileManager = new FileBackedTaskManager(new InMemoryHistoryManager(), new File("src\\main\\managers\\file\\fileToSaveTest.CSV"));

        fileManager.createTask(new Task("Task 1", "Описание 1"));
        fileManager.createTask(new Task("Task 2", "Описание 2"));
        fileManager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        fileManager.createEpic(new Epic("Эпик 2", "без подзадач"));

        fileManager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));
        fileManager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));

        fileManager.updateSubtask(new Subtask("Купить коробки1", "Размер 350х350х300", 5, Status.IN_PROGRESS));
        fileManager.updateSubtask(new Subtask("Купить скотч1", "5 шт. - прозрачного", 6, Status.DONE));
        fileManager.updateSubtask(new Subtask("Составить план1", "Начертить схему", 7, Status.DONE));

        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());

        fileManager.deleteAllTasks();
        fileManager.deleteAllEpic();
        fileManager.deleteAllSubtask();
        System.out.println("Очистка \n");

        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());

        String fileTest = "fileToSaveTest.CSV";
        fileManager.loadFromFile(fileTest); // передаём имя файла в метод

        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());



        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtask());*/

    }
}