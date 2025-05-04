import Task.Task;
import Task.Epic;
import Task.Subtask;
import Task.Status;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Купить продукты", "Молоко, хлеб, помидоры", 1, Status.NEW);
        Task task2 = new Task("Провести тренировку", "Подтягивание, отжимание, приседания", 2, Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Переезд", "В другой офис", 3, Status.NEW);
        Epic epic2 = new Epic("Ремонт на даче", "Составить список материалов", 4, Status.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Купить коробки", "Размер 350х350х300", 5, Status.NEW, 3);
        Subtask subtask2 = new Subtask("Купить скотч", "5 шт. - прозрачного", 6, Status.NEW, 3);
        Subtask subtask3 = new Subtask("Составить план", "Начертить схему", 7, Status.NEW, 4);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        System.out.println("1 вывод");
        //manager.getAllTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        Task task3 = new Task("Купить продукты", "Молоко, хлеб, помидоры", 1, Status.DONE);
        Task task4 = new Task("Провести тренировку", "Подтягивание, отжимание, приседания", 2, Status.IN_PROGRESS);
        manager.updateTask(task3);
        manager.updateTask(task4);

        System.out.println("2 вывод");
       // manager.getAllTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");

        Subtask subtask4 = new Subtask("Купить коробки", "Размер 350х350х300", 5, Status.IN_PROGRESS, 3);
        Subtask subtask5 = new Subtask("Купить скотч", "5 шт. - прозрачного", 6, Status.DONE, 3);
        Subtask subtask6 = new Subtask("Составить план", "Начертить схему", 7, Status.DONE, 4);
        manager.updatingSubtask(subtask4);
        manager.updatingSubtask(subtask5);
        manager.updatingSubtask(subtask6);

        System.out.println("3 вывод");
        //manager.getAllTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");

        //manager.getAllTasksId(3);// поиск через id по всем задачам
       // manager.getTaskById(3);
       // manager.getEpicById(3);
      //  manager.getSubtaskById(3);

        //manager.ListOfEpicSubtasks (3); // Получение списка всех подзадач определённого эпика
        System.out.println(manager.listOfEpicSubtasks(3));

        manager.deleteByIdTask(1); // Удаляем задачу
        manager.deleteByIdEpic(3); // Удаляем Эпик

        System.out.println("4 вывод");
       // manager.getAllTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");
    }
}