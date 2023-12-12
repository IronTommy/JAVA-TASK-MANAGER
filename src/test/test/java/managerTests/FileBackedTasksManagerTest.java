package test.java.managerTests;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.inFile.FileBackedTasksManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest{

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTasksManager();
    }

    @Test
    public void putHistoryInFile() throws IOException {
        Task task = new Task("Задача", "Описание",
                Status.NEW, 1, "PT1H", "2016-08-04T10:11:30");
        try {
            taskManager.putTask(task);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        taskManager.showMeTaskById(1);
        BufferedReader br = new BufferedReader(new FileReader(
                "logs/logHistory.CSV", StandardCharsets.UTF_8));
        String line = br.readLine();
        br.close();
        if (line == null) {
            throw new IOException("Список пуст");
        }
        assertEquals("1 ,", line);
    }

    @Test
    public void putTaskInFile() throws IOException {
        Task task = new Task("Задача", "Описание",
                Status.NEW, 1);
        try {
            taskManager.putTask(task);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
        taskManager.showMeTaskById(1);
        BufferedReader br = new BufferedReader(new FileReader(
                "logs/log.CSV", StandardCharsets.UTF_8));
        String line = br.readLine();
        br.close();
        assertEquals("Task , Задача , Описание , 1 , NEW , PT0S , 2020-01-01T00:00 , 2020-01-01T00:00",
                line);
    }



}
