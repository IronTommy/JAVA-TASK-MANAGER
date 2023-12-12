package test.java.managerTests;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.inMemory.InMemoryHistoryManager;
import service.inMemory.InMemoryTasksManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static service.inFile.FileBackedTasksManager.inMemoryHistoryManager;

public class InMeoryHistoryManagerTest {
    protected InMemoryTasksManager InMemoryTasksManager;
    protected InMemoryHistoryManager InMemoryHistoryManager;

    @BeforeEach
    public void beforeEach() {
        InMemoryTasksManager  = new InMemoryTasksManager();
        InMemoryHistoryManager  = new InMemoryHistoryManager();
    }

    @Test
    public void addHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2012-08-04T10:11:30");
        Task task2 = new Task("Задача", "Описание",
                Status.NEW, 2, "PT1H", "2013-08-04T10:11:30");
        InMemoryTasksManager.putTask(task2);
        InMemoryTasksManager.putTask(task1);
        InMemoryTasksManager.showMeTaskById(1);

        assertEquals(1, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    public void removerTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2011-08-04T10:11:30");
        Task task2 = new Task("Задача", "Описание",
                Status.NEW, 2, "PT1H", "2012-08-04T10:11:30");
        Task task3 = new Task("Задача", "Описание",
                Status.NEW, 3, "PT1H", "2013-08-04T10:11:30");
        Task task4 = new Task("Задача", "Описание",
                Status.NEW, 4, "PT1H", "2014-08-04T10:11:30");

        InMemoryTasksManager.putTask(task1);
        InMemoryTasksManager.putTask(task2);
        InMemoryTasksManager.putTask(task3);
        InMemoryTasksManager.putTask(task4);

        InMemoryTasksManager.showMeTaskById(1);
        InMemoryTasksManager.showMeTaskById(2);
        InMemoryTasksManager.showMeTaskById(3);
        InMemoryTasksManager.showMeTaskById(4);
        InMemoryTasksManager.showMeTaskById(1);
        InMemoryTasksManager.showMeTaskById(1);

        assertEquals(4,
                inMemoryHistoryManager.getHistory().size());

        InMemoryTasksManager.removeFromHistory(task1);
        InMemoryTasksManager.removeFromHistory(task2);
        InMemoryTasksManager.removeFromHistory(task3);

        assertEquals(1,
                inMemoryHistoryManager.getHistory().size());
        assertEquals("[Просто задача{Название ='Задача', Описание ='Описание', Номер =4, Статус ='NEW', " +
                        "Продолжительность(ч) = 1, Дата начала =' 2014-авг.-04 10:11'}]",
                inMemoryHistoryManager.getHistory().toString());

        InMemoryTasksManager.removeFromHistory(task4);

        assertTrue(inMemoryHistoryManager.getHistory().isEmpty());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2016-08-04T10:11:30");
        InMemoryTasksManager.putTask(task);
        InMemoryTasksManager.showMeTaskById(1);
        assertFalse(inMemoryHistoryManager.getHistory().isEmpty());
        InMemoryTasksManager.removeFromHistory(task);
    }



}
