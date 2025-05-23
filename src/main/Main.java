
import managers.Managers;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;
import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;


//  Александр, вопрос. А разве в данном проект main не для меня, как инструмент проверить и понять как работает код?
// Его обязательно содержать чистим перед сдачей на проверку?
// Просто я в нём постоянно, что-то экспериментирую и для этого мне нужен весь закомментированный код.

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        //HistoryManager historyManager = Managers.getDefaultHistory();

        manager.createTask(new Task("Task 1", "Описание 1"));
        manager.createTask(new Task("Task 2", "Описание 2"));
        manager.createEpic(new Epic("Эпик 1", "3 подзадачи"));
        manager.createEpic(new Epic("Эпик 2", "без подзадач"));
        // manager.createEpic(new Epic("name", "description", 1));

        manager.createSubtask(new Subtask("Подзадача 1", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 2", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 3", "принадлежит Эпику 1", 3));
        manager.createSubtask(new Subtask("Подзадача 4", "принадлежит Эпику 2", 4));
        System.out.println("1 вывод");
        //manager.deleteAllSubtask();
        //  System.out.println(manager.getIdHistory);
        manager.getTaskById(1);
        manager.getTaskById(2);
        //  System.out.println(manager.getIdHistory);
        manager.getEpicById(3);
        manager.getEpicById(4);
        // System.out.println(manager.getIdHistory);
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

        manager.deleteByIdTask(1); // Удаляем задачу


        manager.deleteByIdEpic(3); // Удаляем Эпик
        manager.deleteByIdSubtask(6); // Удаляем Подзадачу*/

        //  System.out.println(manager.getIdHistory);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());

        //manager.addIdGetIdHistory1();

        System.out.println("Последние просмотренные задачи: \n" + manager.getHistory() + "\n");
        //  System.out.println(historyManager.last10ViewedTask);


        System.out.println("Конец \n");


        manager.updateTask(new Task("Купить продукты", "Молоко, хлеб, помидоры", 1, Status.DONE));
        manager.updateTask(new Task("Провести тренировку", "Подтягивание, отжимание, приседания", 2, Status.IN_PROGRESS));
        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("2 вывод");
        // manager.getAllTasks();
        System.out.println(manager.getTasks());
        //  System.out.println(manager.getEpics());
        //  System.out.println(manager.getSubtask());
        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("Конец \n");

        manager.updateSubtask(new Subtask("Купить коробки1", "Размер 350х350х300", 5, Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Купить скотч1", "5 шт. - прозрачного", 6, Status.DONE));
        manager.updateSubtask(new Subtask("Составить план1", "Начертить схему", 7, Status.DONE));

        System.out.println("3 вывод");
        manager.updateEpic(new Epic("Переезд1", "В другой офис1", 3));


        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");

        //manager.getAllTasksId(3);// поиск через id по всем задачам
        //  manager.getTaskById(3);
        //System.out.println(manager.getEpicById(3));
        // manager.getSubtaskById(3);
        System.out.println(manager.getEpics().get(0).getId());

        //manager.ListOfEpicSubtasks (3); // Получение списка всех подзадач определённого эпика
        //  System.out.println("Список подзадач определённого эпика \n" + manager.listOfEpicSubtasks(3) +"\n");

        // manager.deleteAllSubtask();
        //  manager.deleteByIdTask(1); // Удаляем задачу
        //  manager.deleteByIdEpic(3); // Удаляем Эпик
        // manager.deleteByIdSubtask(6); // Удаляем Подзадачу

        //System.out.println(manager.getEpics());
        //System.out.println(manager.getSubtask());
        //manager.deleteAllTasks();
        //manager.deleteAllEpic();
        //manager.deleteAllSubtask();
        System.out.println("4 вывод");
        //System.out.println("Подзадачи Эпика \n" + manager.getEpicById(3).get().getListSubtaskIds() + "\n" );

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        // System.out.println("Последние просмотренные задачи: \n" + manager.getHistory1());

        System.out.println("Последние просмотренные задачи (новое): \n" + manager.getHistory() + "\n");
        System.out.println("Конец \n");
    }
}