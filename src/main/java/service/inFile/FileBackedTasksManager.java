package service.inFile;

import model.Epic;
import model.SubTask;
import model.Task;
import service.auxiliary.Status;
import service.exception.ManagerSaveException;
import service.inMemory.InMemoryHistoryManager;
import service.inMemory.InMemoryTasksManager;
import service.managers.Managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileBackedTasksManager extends InMemoryTasksManager {
    public static InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    static FileBackedTasksManager fileBackedTasksManager;

    @Override
    public void putTask(Task task) throws IOException, InterruptedException {
        try {
            super.putTask(task);
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        save();
    }

    @Override
    public void putSubTask(SubTask subtask) throws IOException, InterruptedException {
        try {
            super.putSubTask(subtask);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода. " + e.getMessage());
        }
        save();
    }

    @Override
    public void updateTask(Task task) {
        try {
            super.updateTask(task);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода. " + e.getMessage());
        }
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        try {
            super.updateTask(epic);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода. " + e.getMessage());
        }
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        try {
            super.updateSubTask(subTask);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода. " + e.getMessage());
        }
        save();
    }

    @Override
    public void removeTaskById(long id) throws IOException, InterruptedException {
        try {
            super.removeTaskById(id);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода");
        }
        save();
        saveHistory();
    }

    @Override
    public void removeAll() {
        super.removeAll();
        inMemoryHistoryManager.linkedTasks.clear();
        save();
    }

    @Override
    public Task showMeTaskById(long id) throws IOException {
        Task showMe = super.showMeTaskById(id);
        saveHistory();
        return showMe;
    }

    @Override
    public ArrayList<Task> showMeAllTasks() {
        return super.showMeAllTasks();
    }


    @Override
    public ArrayList<Task> showMeAllEpics() {
        return super.showMeAllEpics();
    }

    @Override
    public ArrayList<SubTask> showMeAllSubtaskInEpic(Epic epic) {
        return super.showMeAllSubtaskInEpic(epic);
    }

    static String toString(Task task) {

        String SubtaskList;
        if (task.getClass() == Task.class) {
            return "Task , " + task.getName() + " , " + task.getDescription() + " , " +
                    task.getId() + " , " + task.getStatus() + " , " + task.getDuration() + " , " + task.getStartTime() +
                    " , " + task.getEndTime() + "\n";

        } else if (task.getClass() == Epic.class) {
            StringBuilder SubtaskListBuilder = new StringBuilder("Epic , " + task.getName() + " , " + task.getDescription() + " , " +
                    task.getId() + " , " + task.getStatus() + " , " + task.getDuration() + " , " + task.getStartTime() +
                    " , " + task.getEndTime() + "\n");
            for (SubTask x : ((Epic) task).subTasksList) {
                SubtaskListBuilder.append("SubTask , ").append(x.getName()).append(" , ").append(x.getId()).append(" , ").append(x.getStatus()).append(" , ").append(x.getEpicId()).append(" , ").append(x.getDuration()).append(" , ").append(x.getStartTime()).append(" , ").append(x.getEndTime()).append("\n");
            }
            SubtaskList = SubtaskListBuilder.toString();
            return SubtaskList;
        } else {
            SubtaskList = task.getName() + " , " + task.getDescription() + " , " +
                    task.getId() + " , " + ((SubTask) task).getEpicId() + " , "
                    + task.getStatus() + " , " + task.getDuration() + " , " + task.getStartTime() +
                    " , " + task.getEndTime() + "\n";
        }
        return SubtaskList;
    }

    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    public Set<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    private void save() {
        try {
            Writer fileWriter = new FileWriter("logs/log.CSV", StandardCharsets.UTF_8);
            for (Task x : super.tasks.values()) { //вызов супер//TODO
                if (x.getClass() == Task.class) {
                    fileWriter.append(toString(x));
                } else if (x.getClass() == Epic.class) {
                    fileWriter.append(toString(x));
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка ввода");
        }
    }

    private void saveHistory() throws IOException {
        Writer fileWriter = new FileWriter("logs/logHistory.CSV", StandardCharsets.UTF_8);

        for (Task x : inMemoryHistoryManager.linkedTasks) {
            fileWriter.append(Integer.toString((int) x.getId()));
            fileWriter.append(" ,");
        }
        fileWriter.close();
    }

    public static void saveCounter() throws IOException {
        Writer fileWriter = new FileWriter("logs/saveCounter.csv", StandardCharsets.UTF_8);
        fileWriter.append(String.valueOf(counter));
        fileWriter.append(" ,");
        fileWriter.close();
    }

    private static void loadCounter() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("logs/saveCounter.csv", StandardCharsets.UTF_8));
        String line = br.readLine();

        if (line == null) {
            Writer fileWriter = new FileWriter("logs/saveCounter.csv");
            fileWriter.write(String.valueOf(0));
            fileWriter.write(" ,");
            fileWriter.close();
        } else {
            String[] splitter = line.split(" ,");
            id = Integer.parseInt(splitter[0]);
        }
    }

    public void loadHistory() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("logs/logHistory.CSV", StandardCharsets.UTF_8));

        while (br.ready()) {
            String line = br.readLine();
            String[] splitter = line.split(" ,");
            for (String s : splitter) {
                if (super.tasks.get(Long.parseLong(s)) == null) {
                    for (Task x : super.tasks.values()) {
                        if (x.getClass() == Epic.class) {
                            Epic epic = (Epic) x;
                            for (int y = 0; y < epic.subTasksList.size(); y++) {
                                if (epic.subTasksList.get(y).getId() == Long.parseLong(s)) {
                                    inMemoryHistoryManager.add(epic.subTasksList.get(y));
                                }
                            }
                        }
                    }
                } else {
                    inMemoryHistoryManager.add(super.tasks.get(Long.parseLong(s)));
                }
            }
        }
    }

    public FileBackedTasksManager loadFromFile() throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader("logs/log.CSV"));
        fileBackedTasksManager = new FileBackedTasksManager();
        loadCounter();
        while (br.ready()) {
            String line = br.readLine();
            String[] splitter = line.split(" , ");
            switch (splitter[0]) {
                case "Task": {
                    fileBackedTasksManager.putTask(new Task(splitter[1], splitter[2], Status.valueOf(splitter[4]),
                            Integer.parseInt(splitter[3]), splitter[5], splitter[6]));
                }
                case "Epic":
                    fileBackedTasksManager.putTask(new Epic(splitter[1], splitter[2], Status.valueOf(splitter[4]),
                            Integer.parseInt(splitter[3]), splitter[5], splitter[6]));
                case "SubTask": {
                    fileBackedTasksManager.putSubTask(new SubTask(splitter[1], Status.valueOf(splitter[3]),
                            Integer.parseInt(splitter[4]), Integer.parseInt(splitter[2]),
                            splitter[5], splitter[6]));
                }
            }
        }
        loadHistory();
        return fileBackedTasksManager;
    }
}
