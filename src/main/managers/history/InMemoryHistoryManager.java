package managers.history;

import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager { // Реализация истории просмотров в памяти
    // 6-ой спринт. Собственная реализация двух-связного списка задач (аналог LinkedHashMap) с методами linkLast.
    private HashMap<Integer, Node<Task>> nodeMap = new HashMap<>(); // создаём таблицу для узлов

    private Node<Task> head; // Указатель на первый элемент списка.
    private Node<Task> tail; // Указатель на последний элемент списка.

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
        ArrayList<Task> listTaskHistory = new ArrayList<>(nodeMap.size());
        Node<Task> temp = tail; // последний узел
        while (temp != null) {
            listTaskHistory.add(temp.data);
            temp = temp.prev; // идёт обратно по списку от последнего к первому
        }
        Collections.reverse(listTaskHistory); // Переворачиваем порядок элементов для верного отображения
        return listTaskHistory;
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) { // проверяем, если такой id есть в таблице
            Node<Task> nodeId = nodeMap.remove(id); // удалили из map и получили узел
            if (nodeId.prev == null && nodeId.next == null) {
                tail = null; // Суть этой строки, что она удаляет оставшийся единственный узел.
            } else if (nodeId.prev == null) { // удаляем первый элемент
                nodeId.next.prev = null;
            } else if (nodeId.next == null) { // удаляем последний элемент
                nodeId.prev.next = null;
                tail= nodeId.prev; // теперь этот узел становиться последним
            } else { // удаляем средний элемент
                nodeId.next.prev = nodeId.prev;
                nodeId.prev.next = nodeId.next;
            }
        }
    }

    private void linkLast(Task element) {
        final Node<Task> newNode = new Node<>(null, new Task(element), null); // создаём новый Node c копией Task
        if (tail != null) { // Если список не пуст
            tail.next = newNode; // указываем старый tail ссылку на новый Node
            newNode.prev = tail; // указываем новому Node ссылку на старый tail
        }
        tail = newNode; // tail становиться и заголовком и хвостом списка
        nodeMap.put(element.getId(), tail); // узел добавляется в Map с присвоением индекса
    }
}