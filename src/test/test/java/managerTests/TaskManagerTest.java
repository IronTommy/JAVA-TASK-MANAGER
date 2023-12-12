
package test.java.managerTests;


import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.exception.ManagerSaveException;
import service.managers.TaskManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

abstract class TaskManagerTest {
    protected TaskManager taskManager;

    @Test
    void putTaskTest() throws IOException {
        Task task = new Task("Задача", "Описание",

                Status.NEW, 1, "PT1H", "2001-08-04T10:11:30");

        assertNull(taskManager.showMeTaskById(task.getId()));

        try {
            taskManager.putTask(task);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(taskManager.showMeTaskById(11));
        Assertions.assertEquals(task, taskManager.showMeTaskById(task.getId()));
    }

    @Test
    void putSubTaskTest() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание", 1);
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(), 2, "PT11H", "2006-08-04T10:11:30");

        try {
            taskManager.putTask(someEpic1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        taskManager.putSubTask(subtask1);

        assertEquals(subtask1, taskManager.showMeTaskById(subtask1.getId()));
        assertNull(taskManager.showMeTaskById(11));

        taskManager.removeAll();
        assertNull(taskManager.showMeTaskById(subtask1.getId()));

    }

    @Test
    void updateTaskTest() throws IOException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, 1, "PT12H", "2002-08-04T10:11:30");
        try {
            taskManager.putTask(task1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        Task task2 = new Task("Задача2", "Описание",
                Status.NEW, 1, "PT12H", "2003-08-04T10:11:30");

        assertNull(taskManager.showMeTaskById(11));

        taskManager.updateTask(task2);

        assertEquals(task2, taskManager.showMeTaskById(1));

        taskManager.removeAll();
        assertNull(taskManager.showMeTaskById(1));
    }

    @Test
    public void updateEpicTest() throws IOException {
        Epic someEpic1 = new Epic("Эпик", "Описание", 1,
                "PT12H", "2004-08-04T14:11:30");
        Epic someEpic2 = new Epic("Эпик2", "Описание", 1,
                "PT12H", "2005-08-04T14:11:30");

        assertNull(taskManager.showMeTaskById(11));

        try {
            taskManager.putTask(someEpic1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        taskManager.updateTask(someEpic2);

        assertEquals(someEpic2, taskManager.showMeTaskById(someEpic2.getId()));

        taskManager.removeAll();
        assertNull(taskManager.showMeTaskById(someEpic2.getId()));
    }

    @Test
    public void updateSubTaskTest() throws IOException {
        Epic someEpic1 = new Epic("Эпик", "Описание");
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(), 2, "PT11H", "2006-08-04T10:11:30");

        assertNull(taskManager.showMeTaskById(11));

        try {
            taskManager.putTask(someEpic1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            taskManager.putSubTask(subtask1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SubTask subtask2 = new SubTask("ПодзадачаНовая", Status.DONE,
                someEpic1.getId(), 2, "PT12H", "2006-08-04T23:11:30");
        taskManager.updateSubTask(subtask2);

        assertEquals(subtask2, taskManager.showMeTaskById(subtask2.getId()));

        taskManager.removeAll();
        assertNull(taskManager.showMeTaskById(subtask2.getId()));
    }

    @Test
    void showMeTaskById() throws IOException {
        Task task = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2001-08-04T10:11:30");

        assertNull(taskManager.showMeTaskById(11));

        try {
            taskManager.putTask(task);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        assertNotNull(taskManager.showMeTaskById(1), "Задача не найдена");
        assertEquals(task, taskManager.showMeTaskById(task.getId()));

        taskManager.removeAll();
        assertNull(taskManager.showMeTaskById(task.getId()));
    }

    @Test
    public void showMeAllSubtaskInEpic() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание",
                "PT12H", "2017-08-04T14:11:30");
        SubTask subtask1 = new SubTask("Эпик",
                Status.NEW, someEpic1.getId(), 2, "PT1H", "2016-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Эпик",
                Status.NEW, someEpic1.getId(), 3, "PT1H", "2016-08-04T12:11:30");

        assertNull(taskManager.showMeTaskById(11));

        ArrayList<Task> TasksForOutput = new ArrayList<>();

        TasksForOutput.add(subtask1);
        TasksForOutput.add(subtask2);

        try {
            taskManager.putTask(someEpic1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        try {
            taskManager.putSubTask(subtask1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            taskManager.putSubTask(subtask2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(TasksForOutput, taskManager.
                showMeAllSubtaskInEpic(someEpic1));

        taskManager.removeTaskById(2);
        taskManager.removeTaskById(3);
        TasksForOutput.clear();

        assertEquals(TasksForOutput, taskManager.
                showMeAllSubtaskInEpic(someEpic1));

    }

    @Test
    public void showMeAllEpics() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание", 1,
                "PT12H", "2016-08-04T14:11:30");
        try {
            taskManager.putTask(someEpic1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        Epic someEpic2 = new Epic("Эпик", "Описание", 2,
                "PT12H", "2016-09-04T14:11:30");
        try {
            taskManager.putTask(someEpic2);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        Task task1 = new Task("Задача", "Описание",
                Status.NEW, 3, "PT1H", "2016-10-04T10:11:30");
        try {
            taskManager.putTask(task1);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(taskManager.showMeTaskById(11));

        ArrayList<Task> TasksForOutput = new ArrayList<>();
        TasksForOutput.add(someEpic1);
        TasksForOutput.add(someEpic2);

        assertEquals(TasksForOutput, taskManager.showMeAllEpics());

        taskManager.removeTaskById(2);
        taskManager.removeTaskById(3);
        TasksForOutput.clear();
    }

    @AfterEach
    void removeAll() throws ManagerSaveException {
        taskManager.removeAll();
    }

    @Test
    void removeTaskById() throws ManagerSaveException, IOException, InterruptedException {
        Task task = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2001-08-04T10:11:30");
        try {
            taskManager.putTask(task);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(task, taskManager.showMeTaskById(task.getId()));
        taskManager.removeTaskById(1);
        assertNull(taskManager.showMeTaskById(task.getId()));
    }
}


