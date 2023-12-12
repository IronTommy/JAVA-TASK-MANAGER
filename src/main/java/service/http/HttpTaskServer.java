package service.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.SubTask;
import model.Task;
import service.auxiliary.Status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpTaskServer {
    private static final int PORT = 8080;
    static HttpServer httpServer = null;
    public Gson gson;
    public final HttpTaskManager httpTaskManager;

    public HttpTaskServer(HttpTaskManager httpTaskManager) throws IOException {
        this.httpTaskManager = httpTaskManager;

        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new HttpTaskManager.TaskSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicSerializer())
                .registerTypeAdapter(SubTask.class, new HttpTaskManager.subTaskSerializer())
                .registerTypeAdapter(LocalDateTime.class, new HttpTaskManager.LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new HttpTaskManager.DurationAdapter())
                .registerTypeAdapter(Task.class, new HttpTaskManager.taskDeSerializer())
                .registerTypeAdapter(Epic.class, new HttpTaskManager.epicDeSerializer())
                .create();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task/", new taskHandler());
        httpServer.createContext("/tasks/epic/", new epicHandler());
        httpServer.createContext("/tasks/subtask/", new subtaskHandler());
        httpServer.createContext("/tasks/tasks/history/", new historyHandler());
        httpServer.createContext("/tasks/prioritized/", new PrioritizedHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    class taskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/task/ запроса от клиента.");
            switch (h.getRequestMethod()) {
                case "GET" -> {
                    String getBody = h.getRequestURI().getQuery();
                    if (getBody == null) {
                        ArrayList<Task> tasks = httpTaskManager.showMeAllTasks();
                        String s = gson.toJson(tasks);
                        sendText(h, s);
                        return;
                    }
                    URI requestURI = h.getRequestURI();
                    String query = requestURI.getQuery();
                    Long id = Long.parseLong(query.split("=")[1]);
                    if (httpTaskManager.tasks.get(id) != null) {

                        Task tasks = httpTaskManager.showMeTaskById(id);
                        if (tasks.getName() == null) {
                            System.out.println("Невалидный_id,_Task.size()= " + httpTaskManager.tasks.size());
                            sendText(h, "Невалидный_id,_Task.size()= " + httpTaskManager.tasks.size());
                            h.close();
                            break;
                        }
                        String s = gson.toJson(httpTaskManager.tasks.get(id));
                        sendText(h, s);
                        h.close();
                        break;
                    }
                    sendText(h, "Такой_задачи_нет_в_списке");
                }
                case "POST" -> {
                    String value = readText(h);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    JsonElement jsonElement = JsonParser.parseString(value);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Task task = new Task();
                    task.setName(jsonObject.get("name").getAsString());
                    task.setDescription(jsonObject.get("description").getAsString());
                    task.setId(jsonObject.get("id").getAsLong());
                    task.setStatus(Status.valueOf(jsonObject.get("status").getAsString()));
                    task.setDuration(Duration.parse(jsonObject.get("duration(h)").getAsString()));
                    task.setStartTime(LocalDateTime.parse(jsonObject.get("start_time").getAsString()));
                    if (httpTaskManager.tasks.get(task.getId()) == null) {
                        httpTaskManager.putTask(task);
                        System.out.println("Задача добавлена");
                    } else {
                        httpTaskManager.updateTask(task);
                        System.out.println("Задача обновлена");
                    }
                    sendText(h, String.valueOf(httpTaskManager.tasks.get(task.getId())));
                    h.close();
                }
                case "DELETE" -> {
                    String deleteDody = h.getRequestURI().getQuery();
                    if (deleteDody == null) {
                        httpTaskManager.removeAll();
                        sendText(h, "Все_задачи_удалены,_Task.size()=_" + httpTaskManager.tasks.size());
                        System.out.println("Все_задачи_удалены,_Task.size()=_" + httpTaskManager.tasks.size());
                        h.close();
                        break;
                    }
                    long deleteById = Long.parseLong(deleteDody.split("=")[1]);
                    httpTaskManager.removeTaskById(deleteById);
                    sendText(h, "Задача_удалена, Task.size()= " + httpTaskManager.tasks.size());
                    h.close();
                }
            }

        }
    }

    class epicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/epic/ запроса от клиента.");

            switch (h.getRequestMethod()) {
                case "GET" -> {
                    String getBody = h.getRequestURI().getQuery();
                    if (getBody == null) {
                        ArrayList<Task> tasks = httpTaskManager.showMeAllEpics();
                        String s = gson.toJson(tasks);
                        sendText(h, s);
                        return;
                    }
                    long id = Long.parseLong(getBody.split("=")[1]);
                    Task tasks = httpTaskManager.showMeTaskById(id);
                    if (tasks == null) {
                        System.out.println("Невалидный_id,_Task.size()= " + httpTaskManager.tasks.size());
                        sendText(h, "Невалидный_id,_Task.size()= " + httpTaskManager.tasks.size());
                        break;
                    }
                    String s = gson.toJson(httpTaskManager.tasks.get(id));
                    sendText(h, s);
                    return;
                }
                case "POST" -> {
                    String value = readText(h);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    JsonElement jsonElement = JsonParser.parseString(value);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray jsonArray = (JsonArray) jsonObject.get("subTask");
                    Epic someEpic1 = new Epic(jsonObject.get("name").getAsString(),
                            jsonObject.get("description").getAsString(),
                            jsonObject.get("id").getAsLong());
                    if (httpTaskManager.tasks.get(someEpic1.getId()) == null) {
                        httpTaskManager.putTask(someEpic1);

                        for (JsonElement x : jsonArray) {
                            JsonObject JsonSubtak = x.getAsJsonObject();
                            SubTask subTask = new SubTask(JsonSubtak.get("name").getAsString(),
                                    Status.valueOf(JsonSubtak.get("status").getAsString()),
                                    JsonSubtak.get("epicId").getAsLong(),
                                    JsonSubtak.get("id").getAsLong(),
                                    JsonSubtak.get("duration(h)").getAsString(),
                                    JsonSubtak.get("start_time").getAsString()
                            );
                            httpTaskManager.putSubTask(subTask);
                        }

                    } else {
                        httpTaskManager.updateEpic(someEpic1);
                        for (JsonElement x : jsonArray) {
                            JsonObject JsonSubtak = x.getAsJsonObject();
                            SubTask subTask = new SubTask(JsonSubtak.get("name").getAsString(),
                                    Status.valueOf(JsonSubtak.get("status").getAsString()),
                                    JsonSubtak.get("epicId").getAsLong(),
                                    JsonSubtak.get("id").getAsLong(),
                                    JsonSubtak.get("duration(h)").getAsString(),
                                    JsonSubtak.get("start_time").getAsString()
                            );
                            httpTaskManager.updateSubTask(subTask);
                        }
                    }
                    sendText(h, httpTaskManager.tasks.get(someEpic1.getId()).toString());
                }
                case "DELETE" -> {
                    String deleteBody = h.getRequestURI().getQuery();
                    if (deleteBody == null) {
                        httpTaskManager.removeAll();
                        sendText(h, "Задача_удалена,_Task.size()=_" + httpTaskManager.tasks.size());
                        return;
                    }
                    long deleteById = Long.parseLong(deleteBody.split("=")[1]);
                    httpTaskManager.removeTaskById(deleteById);
                    sendText(h, "Задача_удалена, Task.size()= " + httpTaskManager.tasks.size());
                }
            }
            h.close();
        }
    }

    class subtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/subtask/ запроса от клиента.");

            switch (h.getRequestMethod()) {
                case "GET" -> {
                    URI requestURI = h.getRequestURI();
                    String query = requestURI.getQuery();
                    if (query == null) {
                        System.out.println("Невалидный_id");
                        break;
                    }
                    String idStr = query.split("=")[1];
                    int id = Integer.parseInt(idStr);
                    ArrayList<SubTask> subtask = null;
                    ArrayList<Task> tasks = httpTaskManager.showMeAllEpics();
                    for (Task x : tasks) {
                        if (x.getId() == id) {
                            subtask = httpTaskManager.showMeAllSubtaskInEpic((Epic) x);
                            httpTaskManager.showMeAllSubtaskInEpic((Epic) x);
                        }
                    }
                    assert subtask != null;
                    sendText(h, subtask.toString());
                }
                case "POST" -> {
                    String value = readText(h);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    JsonElement jsonElement = JsonParser.parseString(value);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    SubTask subTask = new SubTask(jsonObject.get("name").getAsString(),
                            Status.valueOf(jsonObject.get("status").getAsString()),
                            jsonObject.get("epicId").getAsLong(),
                            jsonObject.get("id").getAsLong(),
                            jsonObject.get("duration(h)").getAsString(),
                            jsonObject.get("start_time").getAsString());
                    httpTaskManager.updateSubTask(subTask);
                    sendText(h, subTask.toString());
                    h.close();
                }
            }

        }
    }

    class historyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/tasks/history/ запроса от клиента.");
            if ("GET".equals(h.getRequestMethod())) {
                List<Task> tasks = httpTaskManager.getHistory();
                String s = gson.toJson(tasks);
                sendText(h, s);
                return;
            }
            h.close();
        }
    }

    class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange h) throws IOException {
            System.out.println("Началась обработка /tasks/prioritized/ запроса от клиента.");
            if ("GET".equals(h.getRequestMethod())) {
                Set<Task> tasks = httpTaskManager.getPrioritizedTasks();
                String s = gson.toJson(tasks);
                sendText(h, s);
                return;
            }
            h.close();
        }
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void start() {
        httpServer.start();
    }

    public static void stop() {
        httpServer.stop(0);
    }

}

