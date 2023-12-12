package test.java.httpTests;

import KVServer.KVServer;
import com.google.gson.*;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.auxiliary.Status;
import service.http.HttpTaskManager;
import service.http.KVTaskClient;
import service.inFile.FileBackedTasksManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static service.http.KVTaskClient.client;

public class HttpTaskanagerTest extends FileBackedTasksManager {
    KVServer KVServer;
    static HttpTaskManager HttpTaskManager;
    KVTaskClient KVTaskClient;
    String API_KEY;
    private Gson gson;

    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        KVServer = new KVServer();
        KVServer.start();
        KVTaskClient = new KVTaskClient();
        API_KEY = KVTaskClient.getApiKey();
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new HttpTaskManager.TaskSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicSerializer())
                .registerTypeAdapter(SubTask.class, new HttpTaskManager.subTaskSerializer())
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskManager.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new HttpTaskManager.DurationAdapter())
                .registerTypeAdapter(Task.class, new HttpTaskManager.taskDeSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicDeSerializer())
                .create();

        HttpTaskManager = new HttpTaskManager(false);
    }

    @AfterEach
    public void afterEach() {
        KVServer.stop(0);
    }

    @Test
    public void putTasksTest() throws IOException, InterruptedException {
        HashMap<Long, Task> tasksTest = new HashMap<>();
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        tasksTest.put(task1.getId(),task1);
        HttpTaskManager.putTask(task1);

        URI url = URI.create("http://localhost:8078/load/tasks?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        service.http.KVTaskClient.load("tasks");
        assertEquals(HttpTaskManager.getHashMap(), tasksTest);


    }

    @Test
    public void putEpicTest() throws IOException, InterruptedException {
        HashMap<Long, Task> tasksTest = new HashMap<>();
        Epic someEpic1 = new Epic("Эпик", "Описание");
        HttpTaskManager.putTask(someEpic1);
        tasksTest.put(someEpic1.getId(),someEpic1);

        URI url = URI.create("http://localhost:8078/load/tasks?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        service.http.KVTaskClient.load("tasks");
        assertEquals(HttpTaskManager.getHashMap(), tasksTest);

    }


    @Test
    public void removeTaskByIdTest() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание");
        HttpTaskManager.putTask(someEpic1);
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        HttpTaskManager.putTask(task1);

        URI url = URI.create("http://localhost:8078/load/tasks?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        HttpTaskManager.removeAll();
        assertEquals(true, tasks.isEmpty());
    }

    @Test
    public void reoveAllTest() throws IOException, InterruptedException {
        Epic someEpic1 = new Epic("Эпик", "Описание");
        HttpTaskManager.putTask(someEpic1);

        URI url = URI.create("http://localhost:8078/load/tasks?API_KEY=" + API_KEY);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpTaskManager.removeTaskById(someEpic1.getId());

        assertEquals(200, response.statusCode());
        ArrayList<Task> tasks = HttpTaskManager.showMeAllEpics();
        assertEquals(0, tasks.size());
    }

    @Test
    public void loadTest() throws IOException, InterruptedException {
        HttpTaskManager.removeAll();

        Epic someEpic1 = new Epic("Эпик", "Описание");
        HttpTaskManager.putTask(someEpic1);
        SubTask subtask1 = new SubTask("Подзадача1", Status.NEW,
                someEpic1.getId(), 4, "PT11H", "2006-08-04T10:11:30");
        HttpTaskManager.putSubTask(subtask1);
        Task task1 = new Task("Задача", "Описание",
                Status.NEW, "PT15H", "2052-08-04T08:11:30");
        HttpTaskManager.putTask(task1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");
        HashMap<Long, Task> tasks2 = new HashMap<>();
        String nejsonTasks = String.valueOf(service.http.KVTaskClient.load("tasks"));
        JsonElement jsonElement = JsonParser.parseString(nejsonTasks);
        JsonArray jsonObject = jsonElement.getAsJsonArray();

        for (JsonElement x : jsonObject.getAsJsonArray()) {
            JsonObject JsonTask = x.getAsJsonObject();
            if (JsonTask.get("subTasksList") == null) {
                Task task = new Task();

                task.setName(JsonTask.get("name").getAsString());
                task.setDescription(JsonTask.get("description").getAsString());
                task.setId(JsonTask.get("id").getAsLong());
                task.setStatus(Status.valueOf(JsonTask.get("status").getAsString()));
                task.setDuration(Duration.ofHours(Long.parseLong(JsonTask.get("duration(h)").getAsString())));
                task.setStartTime(LocalDateTime.parse(JsonTask.get("start_time").getAsString(), formatter));
                task.setEndTime(LocalDateTime.parse(JsonTask.get("end_time").getAsString(), formatter));

                tasks2.put(task.getId(), task);
            } else {
                Epic epic = new Epic();
                epic.setName(JsonTask.get("name").getAsString());
                epic.setDescription(JsonTask.get("description").getAsString());
                epic.setId(JsonTask.get("id").getAsLong());
                epic.setStatus(Status.valueOf(JsonTask.get("status").getAsString()));
                epic.setDuration(Duration.ofHours(Long.parseLong(JsonTask.get("duration(h)").getAsString())));
                epic.setStartTime(LocalDateTime.parse(JsonTask.get("start_time").getAsString(), formatter));
                epic.setEndTime(LocalDateTime.parse(JsonTask.get("end_time").getAsString(), formatter));
                ArrayList<SubTask> subTaskList = new ArrayList<>();

                JsonArray jsonArray = (com.google.gson.JsonArray) JsonTask.get("subTasksList");


                for (JsonElement subtask : jsonArray) {
                    JsonObject JsonSubtak = subtask.getAsJsonObject();

                    SubTask newSubtask = new SubTask();
                    newSubtask.setName(JsonSubtak.get("name").getAsString());
                    newSubtask.setId(JsonSubtak.get("id").getAsLong());
                    newSubtask.setStatus(Status.valueOf(JsonSubtak.get("status").getAsString()));
                    newSubtask.setDuration(Duration.ofHours(Long.parseLong(JsonSubtak.get("duration(h)").getAsString())));
                    newSubtask.setStartTime(LocalDateTime.parse(JsonSubtak.get("start_time").getAsString(), formatter));
                    subTaskList.add(newSubtask);
                }
                epic.setSubTasksList(subTaskList);
                tasks2.put(epic.getId(), epic);
            }
        }

        assertEquals(HttpTaskManager.getHashMap(), tasks2);
    }
}

