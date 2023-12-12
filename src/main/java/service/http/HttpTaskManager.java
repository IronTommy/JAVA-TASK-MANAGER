package service.http;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Epic;
import model.SubTask;
import model.Task;
import service.auxiliary.Status;
import service.exception.ManagerSaveException;
import service.inFile.FileBackedTasksManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;


public class HttpTaskManager extends FileBackedTasksManager {
    protected boolean load;
    Gson gson;

    public HttpTaskManager(boolean load) {
        this.load = load;

        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskSerializer())
                .registerTypeAdapter(Epic.class, new epicSerializer())
                .registerTypeAdapter(SubTask.class, new subTaskSerializer())
                .registerTypeAdapter(SubTask.class, new subTaskSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Task.class, new taskDeSerializer())
                .registerTypeAdapter(Epic.class, new epicDeSerializer())
                .create();

        if (load) {
            try {
                load();
            } catch ( IOException | InterruptedException e) {
                throw new RuntimeException("Ошибка ввода. " + e.getMessage());
            }
        }

    }

    @Override
    public void putTask(Task task) {
        try {
            super.putTask(task);
            String jsonString = gson.toJson(tasks.values());
            service.http.KVTaskClient.save("tasks", jsonString);
        } catch ( IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка ввода. " + e.getMessage());
        }
    }

    @Override
    public void putSubTask(SubTask subTask) {
        try {
            super.putSubTask(subTask);
            String jsonString = gson.toJson(tasks.values());
            service.http.KVTaskClient.save("tasks", jsonString);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка ввода. " + e.getMessage());
        }
    }

    @Override
    public void removeTaskById(long id) {
        try {
            super.removeTaskById(id);
            String jsonString = gson.toJson(tasks.values());
            service.http.KVTaskClient.save("tasks", jsonString);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка ввода. " + e.getMessage());
        }
    }


    @Override
    public void removeAll() {
        try {
            super.removeAll();
            String jsonString = gson.toJson(tasks.values());
            service.http.KVTaskClient.save("tasks", jsonString);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Ошибка ввода. " + e.getMessage());
        }
    }

    public void load() throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");
        HashMap<Long, Task> tasks2 = new HashMap<>();

        String nejsonTasks = String.valueOf(service.http.KVTaskClient.load("tasks"));

        JsonElement jsonElement = JsonParser.parseString(nejsonTasks);
        JsonObject jsonObject = jsonElement.getAsJsonObject();


        JsonArray jsonArrayTask = (JsonArray) jsonObject.get("Task");
        JsonArray jsonArrayEpic = (JsonArray) jsonObject.get("Epic");

        for (JsonElement x : jsonArrayTask) {
            JsonObject JsonTask = x.getAsJsonObject();
            Task task = new Task();

            task.setName(JsonTask.get("name").getAsString());
            task.setDescription(JsonTask.get("description").getAsString());
            task.setId(JsonTask.get("id").getAsLong());
            task.setStatus(Status.valueOf(JsonTask.get("status").getAsString()));
            task.setDuration(Duration.ofHours(Long.parseLong(JsonTask.get("duration(h)").getAsString())));
            task.setStartTime(LocalDateTime.parse(JsonTask.get("start_time").getAsString(), formatter));
            task.setEndTime(LocalDateTime.parse(JsonTask.get("end_time").getAsString(), formatter));

            tasks2.put(task.getId(), task);
        }

        for (JsonElement x : jsonArrayEpic) {
            JsonObject JsonEpic = x.getAsJsonObject();
            Epic epic = new Epic();
            epic.setName(JsonEpic.get("name").getAsString());
            epic.setDescription(JsonEpic.get("description").getAsString());
            ArrayList<SubTask> subTaskList = new ArrayList<>();
            JsonArray jsonArray = (JsonArray) JsonEpic.get("subTaskList");
            for (JsonElement subtask : jsonArray) {
                JsonObject JsonSubtak = subtask.getAsJsonObject();
                SubTask newSubtask = new SubTask(JsonSubtak.get("name").getAsString(),
                        Status.valueOf(JsonSubtak.get("status").getAsString()),
                        JsonSubtak.get("epicId").getAsLong(),
                        JsonSubtak.get("id").getAsLong(),
                        JsonSubtak.get("duration(h)").getAsString(),
                        JsonSubtak.get("start_time").getAsString());

                subTaskList.add(newSubtask);
            }
            epic.setSubTasksList(subTaskList);

            epic.setId(JsonEpic.get("id").getAsLong());
            epic.setStatus(Status.valueOf(JsonEpic.get("status").getAsString()));
            epic.setDuration(Duration.ofHours(Long.parseLong(JsonEpic.get("duration(h)").getAsString())));
            epic.setStartTime(LocalDateTime.parse(JsonEpic.get("start_time").getAsString(), formatter));
            epic.setEndTime(LocalDateTime.parse(JsonEpic.get("end_time").getAsString(), formatter));
            tasks2.put(epic.getId(), epic);
        }

        this.tasks = tasks2;
    }

    public static class taskDeSerializer implements JsonDeserializer<Task> {
        @Override
        public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject jsonObject = json.getAsJsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");


            Task task = new Task();

            task.setName(jsonObject.get("name").getAsString());
            task.setDescription(jsonObject.get("description").getAsString());
            task.setId(jsonObject.get("id").getAsLong());
            task.setStatus(Status.valueOf(jsonObject.get("status").getAsString()));
            task.setDuration(Duration.ofHours(Long.parseLong(jsonObject.get("duration(h)").getAsString())));
            task.setStartTime(LocalDateTime.parse(jsonObject.get("start_time").getAsString(), formatter));
            task.setEndTime(LocalDateTime.parse(jsonObject.get("end_time").getAsString(), formatter));
            return task;
        }
    }

    public static class epicDeSerializer implements JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject jsonObject = json.getAsJsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");

            Epic epic = new Epic();
            epic.setName(jsonObject.get("name").getAsString());
            epic.setDescription(jsonObject.get("description").getAsString());
            epic.setId(jsonObject.get("id").getAsLong());
            epic.setStatus(Status.valueOf(jsonObject.get("status").getAsString()));
            epic.setDuration(Duration.ofHours(Long.parseLong(jsonObject.get("duration(h)").getAsString())));
            epic.setStartTime(LocalDateTime.parse(jsonObject.get("start_time").getAsString(), formatter));
            epic.setEndTime(LocalDateTime.parse(jsonObject.get("end_time").getAsString(), formatter));
            ArrayList<SubTask> subTaskList = new ArrayList<>();
            JsonArray jsonArray = (JsonArray) jsonObject.get("subTaskList");
            for (JsonElement subtask : jsonArray) {
                JsonObject JsonSubtak = subtask.getAsJsonObject();
                SubTask newSubtask = new SubTask(JsonSubtak.get("name").getAsString(),
                        Status.valueOf(JsonSubtak.get("status").getAsString()),
                        JsonSubtak.get("epicId").getAsLong(),
                        JsonSubtak.get("id").getAsLong(),
                        JsonSubtak.get("duration(h)").getAsString(),
                        JsonSubtak.get("start_time").getAsString());

                subTaskList.add(newSubtask);
            }
            epic.setSubTasksList(subTaskList);
            return epic;


        }
    }

    public static class TaskSerializer implements JsonSerializer<Task> {
        @Override
        public JsonElement serialize(Task src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            result.add("name", context.serialize(src.getName()));
            result.add("description", context.serialize(src.getDescription()));
            result.add("id", context.serialize(src.getId()));
            result.add("status", context.serialize(src.getStatus()));
            result.add("duration(h)", context.serialize(src.getDuration()));
            result.add("start_time", context.serialize(src.getStartTime()));
            result.add("end_time", context.serialize(src.getEndTime()));
            return result;
        }
    }

    public static class epicSerializer implements JsonSerializer<Epic> {
        @Override
        public JsonElement serialize(Epic src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            result.add("name", context.serialize(src.getName()));
            result.add("description", context.serialize(src.getDescription()));
            result.add("id", context.serialize(src.getId()));
            result.add("status", context.serialize(src.getStatus()));
            result.add("duration(h)", context.serialize(src.getDuration()));
            result.add("start_time", context.serialize(src.getStartTime()));
            result.add("end_time", context.serialize(src.getEndTime()));

            JsonArray subTaskList = new JsonArray();
            result.add("subTasksList", subTaskList);

            for (SubTask subtask : src.subTasksList) {
                JsonObject createSubtask = new JsonObject();
                createSubtask.add("name", context.serialize(subtask.getName()));
                createSubtask.add("status", context.serialize(subtask.getStatus()));
                createSubtask.add("id", context.serialize(subtask.getId()));
                createSubtask.add("epicId", context.serialize(subtask.getEpicId()));
                createSubtask.add("duration(h)", context.serialize(subtask.getDuration()));
                createSubtask.add("start_time", context.serialize(subtask.getStartTime()));
                createSubtask.add("end_time", context.serialize(subtask.getEndTime()));
                subTaskList.add(createSubtask);
            }
            return result;
        }
    }

    public static class subTaskSerializer implements JsonSerializer<SubTask> {
        @Override
        public JsonElement serialize(SubTask src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            result.add("name", context.serialize(src.getName()));
            result.add("description", context.serialize(src.getDescription()));
            result.add("status", context.serialize(src.getStatus()));
            result.add("id", context.serialize(src.getId()));
            result.add("epicId", context.serialize(src.getEpicId()));
            result.add("duration(h)", context.serialize(src.getDuration()));
            result.add("start_time", context.serialize(src.getStartTime()));
            result.add("end_time", context.serialize(src.getEndTime()));
            return result;
        }
    }

    public static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");

        @Override
        public void write(final JsonWriter jsonWriter, LocalDateTime localDate) throws IOException {
            jsonWriter.value(localDate.format(formatter));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), formatter);
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            jsonWriter.value(duration.toHours());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            return Duration.ofHours(jsonReader.nextLong());
        }
    }

}
