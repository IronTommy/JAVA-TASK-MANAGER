package test.java;

import model.Epic;
import model.SubTask;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.inMemory.InMemoryTasksManager;
import service.managers.Managers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    InMemoryTasksManager inMemoryTasksManager = Managers.getDafault();

    @Test
    public void epicIsEmpty() throws IOException, InterruptedException {
        Epic someEpic = new Epic("Эпик", "Описание",
                Status.NEW, 1, "PT1H", "2016-08-04T10:11:30");
        inMemoryTasksManager.putTask(someEpic);
        assertEquals(Status.NEW, someEpic.getStatus());
    }

    @Test
    public void epicAllTaskIsNEW() throws IOException, InterruptedException {
        Epic someEpic = new Epic("Эпик", "Описание",
                "PT12H", "2016-08-04T14:11:30");
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic.getId(), "PT12H", "2015-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача2", Status.NEW,
                someEpic.getId(), "PT12H", "2016-08-04T14:11:30");
        inMemoryTasksManager.putTask(someEpic);
        inMemoryTasksManager.putSubTask(subtask1);
        inMemoryTasksManager.putSubTask(subtask2);
        assertEquals(Status.NEW, someEpic.getStatus());
    }

    @Test
    public void epicAllTaskIsDONE() throws IOException, InterruptedException {
        Epic someEpic = new Epic("Эпик", "Описание",
                "PT12H", "2016-08-04T14:11:30");
        SubTask subtask1 = new SubTask("Подзадача1", Status.DONE,
                someEpic.getId(), "PT12H", "2015-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача1", Status.DONE,
                someEpic.getId(), "PT12H", "2015-09-04T10:11:30");
        inMemoryTasksManager.putTask(someEpic);
        inMemoryTasksManager.putSubTask(subtask1);
        inMemoryTasksManager.putSubTask(subtask2);
        assertEquals(Status.DONE, someEpic.getStatus());
    }

    @Test
    public void epicAllTaskIsInProgress() throws IOException, InterruptedException {
        Epic someEpic = new Epic("Эпик", "Описание",
                "PT12H", "2016-08-04T14:11:30");
        SubTask subtask1 = new SubTask("Подзадача1", Status.IN_PROGRESS,
                someEpic.getId(), "PT12H", "2015-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача1", Status.NEW,
                someEpic.getId(), "PT12H", "2015-09-04T10:11:30");
        inMemoryTasksManager.putTask(someEpic);
        inMemoryTasksManager.putSubTask(subtask1);
        inMemoryTasksManager.putSubTask(subtask2);
        assertEquals(Status.IN_PROGRESS, someEpic.getStatus());
    }


}
