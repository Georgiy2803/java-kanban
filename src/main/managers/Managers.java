package managers;
import managers.history.HistoryManager;
import managers.history.InMemoryHistoryManager;
import managers.task.InMemoryTaskManager;
import managers.task.TaskManager;

//  Managers: содержит статические методы для создания различных менеджеров:
public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }
    //  Статический метод getDefaultHistory, который возвращает объект InMemoryHistoryManager — историю просмотров.
    public static HistoryManager getDefaultHistory() { // создаёт экземпляр InMemoryHistoryManager, который реализует интерфейс HistoryManager.
        // каждый раз, когда мы запрашиваем менеджер истории, создаётся новый экземпляр класса InMemoryHistoryManager
        return new InMemoryHistoryManager();
    }
}

/*
Определение из интернета для себя на будущее.
Утилитарный класс в Java — это класс-помощник, содержащий статические переменные и статические методы, которые
выполняют определённый перечень задач, объединённых одним смыслом.
Цель утилитарного класса — предоставить методы для выполнения определённых функций внутри программы, пока основной
класс фокусируется на основной проблеме, которую он решает.

Managers: статический класс для удобного получения экземпляров менеджеров задач и истории.
 */