package service.auxiliary;

import model.Task;

public class Node<T> {

    public T value;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.value = data;
        this.prev = prev;
        this.next = next;

    }

    public Task getValue() {
        return (Task) this.value;
    }
}

