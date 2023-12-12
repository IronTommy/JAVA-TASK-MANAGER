package service.inMemory;

import model.Task;
import service.auxiliary.Node;
import service.managers.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    protected HashMap<Long, Node> nodeMap = new HashMap<>();
    public LinkedList<Task> linkedTasks = new LinkedList<>();
    public Node<Task> last;

    private void linkLast(Task element) {
        Node<Task> oldNode = last;
        Node<Task> newNode = new Node<>(last, element, null);
        if (last == null) {
            last = newNode;
        } else {
            oldNode.next = last;
            last = newNode;
        }
    }

    // для добавления нового просмотра задачи
    @Override
    public void add(Task task) {
        if (nodeMap.isEmpty()) {
            linkLast(task);
            linkedTasks.add(task);
            nodeMap.put(task.getId(), last);
        } else {
            if (nodeMap.containsKey(task.getId())) {
                HashMap<Long, Node> nodeMap2 = new HashMap<>(nodeMap);
                for (Long x : nodeMap2.keySet()) {
                    if (x == task.getId()) {
                        removeNode(nodeMap2.get(task.getId()));
                        linkLast(task);
                        linkedTasks.add(task);
                        nodeMap.put(task.getId(), last);
                    }
                }
            } else {
                linkLast(task);
                linkedTasks.add(task);
                nodeMap.put(task.getId(), last);
            }
        }
    }

    // для удаления просмотра из истории
    @Override
    public void remover(long id) {
        if (nodeMap.get(id) != null) {
            removeNode(nodeMap.get(id));
        }
    }

    // узел связного списка удаляет
    private void removeNode(Node<Task> value) {
        Node<Task> next = value.next;
        Node<Task> prev = value.prev;

        if (prev == null) {
            next = null;
            linkedTasks.remove(value.getValue());
            nodeMap.remove(value.getValue().getId());
        } else if (next == null) {
            prev.next = null;
            linkedTasks.remove(value.getValue());
            nodeMap.remove(value.getValue().getId());
        } else {
            next.prev = prev.next;
            prev.next = next.prev;
            linkedTasks.remove(value.getValue());
            nodeMap.remove(value.getValue().getId());
        }
    }

    // для получения истории последних просмотров.
    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(linkedTasks);
    }
}