package managers.history;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager { // Реализация истории просмотров в памяти
    // 6-ой спринт. Собственная реализация LinkedHashMap
    HashMap<Integer, Node<Task>> nodeMap = new HashMap<>(); // создаём таблицу для узлов

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) { // проверяем, если такой id есть в таблице
            remove(task.getId()); // удаляем узел
            nodeMap.remove(task.getId()); // удаляем id из таблицы nodeMap
        }
        linkLast(task); // добавляем задачу
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    // Реализация аналога двусвязного списка задач с методами linkLast и getTasks. (Подсказка 2).
    Node<Task> head; // Указатель на первый элемент списка.
    Node<Task> tail; // Указатель на последний элемент списка.
    int size = 0; // размер списка

    public void linkLast(Task element) {
        final Node<Task> newNode = new Node<>(null, new Task(element), null); // создаём новый Node c копией Task
        if (tail == null) { // Если список пуст, новый узел становится:
            head = newNode; // и заголовком
            nodeMap.put(element.getId(), head);
            tail = newNode; // и хвостом списка
        } else { // Иначе, добавляем новый узел в конец списка
            tail.next = newNode; // указываем старый tail ссылку на новый Node
            newNode.prev = tail; // указываем новому Node ссылку на старый tail
            tail = newNode; // Обновляем хвост списка
            nodeMap.put(element.getId(), tail);
        }
        size++; // Увеличиваем размер списка на один
    }

    /* Вопрос к Вам Александр.
    Я этот метод могу просто перейменовать в getHistory, и не вызывать его там.
    Но в подсказке чётко сказано, что надо создать метод с таким названием (getTasks).
    Могу ли я не сделать по нормальному? А то у меня это какая-то ерунда выходит. Метод на методе и методом погоняет.
    И что лучше оставить в этом случае, названия которые в интерфейсе или подсказках?  Я бы выбрал Интерфейс.*/
    public ArrayList<Task> getTasks() {
        ArrayList<Task> listTaskHistory = new ArrayList<>(size);
        Node<Task> temp = head;
        while (temp != null) {
            listTaskHistory.add(temp.data);
            temp = temp.next;
        }
        return listTaskHistory;
    }

    /* Точно такая же ситуация. Подсказа требует, чтобы был метод именно с таким названием (removeNode),
    а интерфейс требует в этом же ФЗ другое название (remove).
    Я могу выбрать один вариант? */
    public void removeNode(int id) {
        if (!nodeMap.containsKey(id)) { // проверяем, если такой id есть в таблице
            return;
        }
        Node<Task> nodeId = nodeMap.get(id);
        if (nodeId.prev == null && nodeId.next == null) {
            head = null;
        } else if (nodeId.prev == null) {
            head = nodeId.next;
            nodeId.next.prev = null;
        } else if (nodeId.next == null) {
            nodeId.prev.next = null;
        } else {
            nodeId.next.prev = nodeId.prev;
            nodeId.prev.next = nodeId.next;
        }
        size--; // Уменьшаем размер списка на один
    }
}

// Помню, что документированный текс не допускается, но пока он мне нужен как набор реализованных решений.
// Как вообще в коде оставлять, то что может тебе пригодиться?

// 5-й спринт
    /*private List<Task> history = new ArrayList<>();

    public static final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_MAX_SIZE) {
            history.remove(0);
        }
        try {
            history.add((Task) task.clone()); // добавляет копию объекта
        } catch (CloneNotSupportedException e) {
            // Обработка исключения или логирование
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    } */

    /*
    // 6-ой спринт. Реализация через LinkedHashMap
    private Map<Integer, Task> historyMap = new LinkedHashMap<>();

    @Override
    public void add(Task task) {
        historyMap.remove(task.getId());
        historyMap.put(task.getId(),task);

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyMap.values());
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }
*/