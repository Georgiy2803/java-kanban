package taskManager;

import taskManager.model.Task;
import taskManager.model.Epic;
import taskManager.model.Subtask;
import taskManager.model.Status;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("Купить продукты", "Молоко, хлеб, помидоры"));
        manager.createTask(new Task("Провести тренировку", "Подтягивание, отжимание, приседания"));

        manager.createEpic(new Epic("Переезд", "В другой офис"));
        manager.createEpic(new Epic("Ремонт на даче", "Составить список материалов"));

        manager.createSubtask(new Subtask("Купить коробки", "Размер 350х350х300", 3));
        manager.createSubtask(new Subtask("Купить скотч", "5 шт. - прозрачного",  3));
        manager.createSubtask(new Subtask("Составить план", "Начертить схему",  4));
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
      //  System.out.println(manager.getIdHistory);
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());

        //manager.addIdGetIdHistory1();

        //System.out.println("Последние просмотренные задачи: \n" + manager.getHistory1() + "\n");
      //  System.out.println(historyManager.last10ViewedTask);
        System.out.println("Последние просмотренные задачи (новое): \n" + historyManager.getHistory()+ "\n");


        System.out.println("Конец \n");

        // Измените статусы созданных объектов, распечатайте их. Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.

        manager.updateTask(new Task("Купить продукты", "Молоко, хлеб, помидоры", 1, Status.DONE));
        manager.updateTask(new Task("Провести тренировку", "Подтягивание, отжимание, приседания", 2, Status.IN_PROGRESS));

        System.out.println("2 вывод");
        // manager.getAllTasks();
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtask());
        System.out.println("Конец \n");

        manager.updateSubtask(new Subtask("Купить коробки1", "Размер 350х350х300", 5, Status.IN_PROGRESS));
        manager.updateSubtask(new Subtask("Купить скотч1", "5 шт. - прозрачного", 6, Status.DONE));
        manager.updateSubtask(new Subtask("Составить план1", "Начертить схему", 7, Status.DONE));

        System.out.println("3 вывод");
        manager.updateEpic(new Epic("Переезд1", "В другой офис1",3));


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

        System.out.println("Последние просмотренные задачи (новое): \n" + historyManager.getHistory()+ "\n");
        System.out.println("Конец \n");
    }
}