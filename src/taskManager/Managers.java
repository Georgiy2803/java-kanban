package taskManager;

/*
Описание в ТЗ.

Утилитарный класс
Со временем в приложении трекера появится несколько реализаций интерфейса TaskManager. Чтобы не зависеть от реализации,
создайте утилитарный класс Managers. На нём будет лежать вся ответственность за создание менеджера задач. То есть
Managers должен сам подбирать нужную реализацию TaskManager и возвращать объект правильного типа.
У Managers будет метод getDefault. При этом вызывающему неизвестен конкретный класс — только то, что объект, который
возвращает getDefault, реализует интерфейс TaskManager.
Подсказка про getDefault.
Метод getDefault будет без параметров. Он должен возвращать объект-менеджер, поэтому типом его возвращаемого значения
будет TaskManager.
 */

//  Managers: содержит статические методы для создания различных менеджеров:
public class Managers {

    // Метод getDefault будет без параметров. Он должен возвращать объект-менеджер, поэтому типом его возвращаемого значения будет TaskManager.
    public static TaskManager getDefault() {
        // Здесь можно добавить логику для выбора конкретной реализации TaskManager
        return new InMemoryTaskManager();
    }

    //  Статический метод getDefaultHistory, который возвращает объект InMemoryHistoryManager — историю просмотров.
    public static HistoryManager getDefaultHistory() {
        // создаёт экземпляр InMemoryHistoryManager, который реализует интерфейс HistoryManager.
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