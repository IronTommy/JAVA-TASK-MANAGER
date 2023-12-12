package service.managers;

import service.http.HttpTaskManager;
import service.inFile.FileBackedTasksManager;
import service.inMemory.InMemoryHistoryManager;
import service.inMemory.InMemoryTasksManager;

import java.io.IOException;

public class Managers {

    public static InMemoryTasksManager getDafault() {
        return new InMemoryTasksManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getBackFileManager() {
        return new FileBackedTasksManager();
    }

    public static HttpTaskManager HttpTaskManager() throws IOException, InterruptedException {
        return new HttpTaskManager(false);
    }
}
