package service.managers;

import model.Epic;
import model.SubTask;
import model.Task;
import service.exception.ManagerSaveException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public interface TaskManager {

    // добавление новой задачи типа ЭПИК И ЗАДАЧА
    void putTask(Task task) throws IOException, ManagerSaveException, URISyntaxException, InterruptedException;

    // добавление подзадачи
    void putSubTask(SubTask subtask1) throws IOException, InterruptedException;

    // обновление задач по айди
    void updateTask(Task task) throws IOException;

    // обновление эпиков по айди
    void updateEpic(Epic epic) throws IOException;

    // обновление подзадач
    void updateSubTask(SubTask subTask) throws IOException;

    // получение списка всех задач
    ArrayList<Task> showMeAllTasks();

    // получение списка всех эпиков
    ArrayList<Task> showMeAllEpics();

    // получение списка всех подзадач у эпика
    ArrayList<SubTask> showMeAllSubtaskInEpic(Epic epic);

    // получение любой задачи по айди
    Task showMeTaskById(long id) throws IOException;

    // удаление ранее добавленных задач — всех
    void removeAll() throws ManagerSaveException;

    // удаление ранее добавленных задач по идентификатору
    void removeTaskById(long id) throws ManagerSaveException, IOException, InterruptedException;


}