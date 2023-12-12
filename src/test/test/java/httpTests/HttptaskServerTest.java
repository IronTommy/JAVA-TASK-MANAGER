package test.java.httpTests;

import KVServer.KVServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.http.HttpTaskManager;
import service.http.HttpTaskServer;
import service.http.KVTaskClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.http.KVTaskClient.client;

public class HttptaskServerTest {
     HttpTaskServer HttpTaskServer;
     public Gson gson;
     HttpTaskManager HttpTaskManager;
     KVServer KVServer;
     KVTaskClient KVTaskClient;

    @BeforeEach
    public  void beforeEach() throws IOException, InterruptedException {
        KVServer = new KVServer();
        KVServer.start();
        KVTaskClient = new KVTaskClient();
        HttpTaskManager = new HttpTaskManager(false);
        HttpTaskServer = new HttpTaskServer(HttpTaskManager);

        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new HttpTaskManager.TaskSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicSerializer())
                .registerTypeAdapter(SubTask.class, new HttpTaskManager.subTaskSerializer())
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskManager.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new HttpTaskManager.DurationAdapter())
                .registerTypeAdapter(Task.class, new HttpTaskManager.taskDeSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicDeSerializer())
                .create();
    }

    @AfterEach
    public void afterEach() {
        HttpTaskManager.removeAll();
        KVServer.stop(0);
        HttpTaskServer.stop();
    }

    @Test
    public void taskHandlerTestGet() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        HttpTaskManager.putTask(task1);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("["+gson.toJson(task1)+"]", response.body());
    }

    @Test
    public void epicHandlerTestGet() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание");
        HttpTaskManager.putTask(someEpic1);

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("["+gson.toJson(someEpic1)+"]", response.body());
    }

    @Test
    public void subTaskHandlerTestGet() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание",1);
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),2, "PT11H", "2006-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),3, "PT11H", "2006-09-04T10:11:30");
        HttpTaskManager.putTask(someEpic1);
        HttpTaskManager.putSubTask(subtask1);
        HttpTaskManager.putSubTask(subtask2);

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(HttpTaskManager.showMeAllSubtaskInEpic(someEpic1).toString(), response.body());
    }

    @Test
    public void historyHandlerTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        HttpTaskManager.putTask(task1);
        Epic someEpic1 = new Epic("Эпик", "Описание");
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),3, "PT11H", "2006-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),4, "PT11H", "2006-09-04T10:11:30");
        HttpTaskManager.putTask(someEpic1);
        HttpTaskManager.putSubTask(subtask1);
        HttpTaskManager.putSubTask(subtask2);
        HttpTaskManager.showMeTaskById(1);
        HttpTaskManager.showMeTaskById(2);

        URI url = URI.create("http://localhost:8080/tasks/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskManager.getHistory()), response.body());

    }

    @Test
    public void prioritizedHandlerTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        HttpTaskManager.putTask(task1);
        Epic someEpic1 = new Epic("Эпик", "Описание");
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),3, "PT11H", "2006-08-04T10:11:30");
        SubTask subtask2 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(),4, "PT11H", "2006-09-04T10:11:30");
        HttpTaskManager.putTask(someEpic1);
        HttpTaskManager.putSubTask(subtask1);
        HttpTaskManager.putSubTask(subtask2);

        URI url = URI.create("http://localhost:8080/tasks/prioritized/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(HttpTaskManager.getPrioritizedTasks()), response.body());
    }
}
