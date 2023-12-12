package service.inMemory;

import model.Epic;
import model.SubTask;
import model.Task;
import service.auxiliary.MyComparator;
import service.auxiliary.Status;
import service.exception.ManagerSaveException;
import service.managers.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import static service.inFile.FileBackedTasksManager.inMemoryHistoryManager;
import static service.inFile.FileBackedTasksManager.saveCounter;


public class InMemoryTasksManager implements TaskManager {
    protected static long id = 0;
    protected static long counter = 1;

    public HashMap<Long, Task> tasks = new HashMap<>();
    Set<Task> prioritizedList = new TreeSet(new MyComparator());

    // этот метод использовался в конструкторе при создании автоматически
    public static long idGenerator() {
        ++id;
        return id;
    }

    // записываем айди в историю
    public void StoreHistory(Task task) {
        inMemoryHistoryManager.add(task);
    }

    // проверка пересечения задач
    public void intesections(Task task) throws IOException {
        if (task.getClass() == SubTask.class) {
            long index = ((SubTask) task).getEpicId();
            Epic epic = (Epic) tasks.get(index);

            for (Task taskInList : tasks.values()) {
                if (taskInList.getEndTime().isBefore(task.getStartTime()) ||
                        task.getEndTime().isBefore(taskInList.getStartTime())) {
                    epic.setEndTime(task.getEndTime());
                    return;
                } else {
                    throw new IOException("Задача пересекается с задачей номер: " + taskInList.getId());
                }
            }
            if (epic.subTasksList.isEmpty()) {
                epic.setEndTime(task.getEndTime());
                return;
            } else {
                for (SubTask x : epic.subTasksList) {
                    if (task.getStartTime().plus(task.getDuration()).
                            isBefore(x.getStartTime().plus(x.getDuration())) ||
                            task.getStartTime().
                                    isAfter(x.getStartTime().plus(x.getDuration()))
                    ) {
                        epic.setEndTime(task.getEndTime());
                        return;
                    } else {
                        throw new IOException("Ваши подзадачи пересекаются. Измените время начала");
                    }
                }
            }
        } else {
            if (!tasks.isEmpty()) {
                for (Task taskInList : tasks.values()) {
                    if (taskInList.getEndTime().isBefore(task.getStartTime()) ||
                            task.getEndTime().isBefore(taskInList.getStartTime())) {
                        return;
                    } else {
                        throw new IOException("Задача пересекается с задачей номер: " + taskInList.getId());
                    }
                }
                if (prioritizedList != null) {
                    for (Task x : prioritizedList) {
                        if (task.getStartTime().plus(task.getDuration()).
                                isBefore(x.getStartTime().plus(x.getDuration())) ||
                                task.getStartTime().
                                        isAfter(x.getStartTime().plus(x.getDuration()))
                        ) {
                            return;
                        } else {
                            throw new IOException("Ваши задачи пересекаются. Измените время начала");
                        }

                    }
                }
            }
        }
    }

    // добавление новой задачи типа ЭПИК И ЗАДАЧА
    @Override
    public void putTask(Task task) throws IOException, InterruptedException {
        intesections(task);
        tasks.put(task.getId(), task);
        prioritizedList.add(task);
    }

    // добавление новой подзадачи
    @Override
    public void putSubTask(SubTask subTask) throws IOException, ManagerSaveException, InterruptedException {
        intesections(subTask);
        long index = subTask.getEpicId();
        Epic forUpdate = (Epic) tasks.get(index);
        setDuration(subTask);
        forUpdate.subTasksList.add(subTask);
        checkEpicStatus((Epic) tasks.get(index));
    }

    // этот метод проверяет только статусы и если нужно меняет их у Эпика.
    protected void checkEpicStatus(Epic epic) {
        int checkNew = 0;
        int checkDone = 0;

        if (epic.subTasksList.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            for (SubTask y : epic.subTasksList) {
                if (y.getStatus() == (Status.NEW)) {
                    checkNew++;
                } else if (y.getStatus() == (Status.DONE)) {
                    checkDone++;
                }
            }
            if (checkDone == epic.subTasksList.size()) {
                epic.setStatus(Status.DONE);
            } else if (checkNew == epic.subTasksList.size()) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    // устанавлвиает продолжительность в зависимости от подзадач
    public void setDuration(SubTask subTask) throws IOException {
        Duration setEpicDuration = subTask.getDuration();
        LocalDateTime setEpicStart = subTask.getStartTime();
        long epicId = subTask.getEpicId();
        Epic epic = (Epic) tasks.get(epicId);

        if (tasks.get(epicId) == null) {
            throw new IOException("Такого Эпика нет");
        } else {
            if (epic.subTasksList.isEmpty()) {
                tasks.get(epicId).setDuration(tasks.get(epicId).
                        getDuration().plus(setEpicDuration));
                tasks.get(epicId).setStartTime(setEpicStart);
            } else {
                if (!epic.subTasksList.isEmpty()) tasks.get(epicId).setDuration(tasks.get(epicId).
                        getDuration().plus(setEpicDuration));
                else {
                    tasks.get(epicId).setStartTime(setEpicStart);
                }
            }
        }
    }

    // обновление задачи
    @Override
    public void updateTask(Task task) throws IOException {
        intesections(task);
        tasks.get(task.getId()).setName(task.getName());
        tasks.get(task.getId()).setDescription(task.getDescription());
        tasks.get(task.getId()).setStatus(task.getStatus());
        tasks.get(task.getId()).setId(task.getId());
        tasks.get(task.getId()).setStartTime(task.getStartTime());
        tasks.get(task.getId()).setDuration(task.getDuration());
    }

    // обновление эпика
    @Override
    public void updateEpic(Epic epic) throws IOException {
        intesections(epic);
        tasks.get(epic.getId()).setName(epic.getName());
        tasks.get(epic.getId()).setDescription(epic.getDescription());
        tasks.get(epic.getId()).setStatus(epic.getStatus());
        checkEpicStatus((Epic) tasks.get(epic.getId()));
    }

    // обновление подзадачи
    @Override
    public void updateSubTask(SubTask subTask) throws IOException {
        intesections(subTask);
        Epic epic = (Epic) tasks.get(subTask.getEpicId());
        for (SubTask x : epic.subTasksList) {
            if (x.getId() == subTask.getId()) {
                x.setName(subTask.getName());
                x.setDescription(subTask.getDescription());
                x.setStatus(subTask.getStatus());
                x.setDuration(subTask.getDuration());
                x.setStartTime(subTask.getStartTime());
                x.setEndTime(subTask.getEndTime());

            }
        }
        checkEpicStatus(epic);
    }

    // получение списка всех задач
    @Override
    public ArrayList<Task> showMeAllTasks() {
        ArrayList<Task> TasksForOutput = new ArrayList<>();

        for (Task x : tasks.values()) {
            if (x.getClass() == Task.class) {
                TasksForOutput.add(x);
            }
        }
        return TasksForOutput;
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedList;
    }

    // получение списка всех эпиков
    @Override
    public ArrayList<Task> showMeAllEpics() {
        ArrayList<Task> TasksForOutput = new ArrayList<>();
        for (Task x : tasks.values()) {
            if (x.getClass() == Epic.class) {
                TasksForOutput.add(x);
            }
        }
        return TasksForOutput;
    }

    // получение любой задачи по айди
    @Override
    public Task showMeTaskById(long id) throws IOException {
        Task returnTask = null;

        if (tasks.get(id) != null) {
            returnTask = tasks.get(id);
            StoreHistory(tasks.get(id));
            return returnTask;
        } else {
            ArrayList<Task> TasksForOutput2 = showMeAllEpics();
            for (Task task : TasksForOutput2) {
                Epic x = (Epic) task;
                for (int y = 0; y < x.subTasksList.size(); y++) {
                    if (x.subTasksList.get(y).getId() == id) {
                        returnTask = x.subTasksList.get(y);
                    }
                }
            }
            if (returnTask == null) {
                System.out.println("Невалидный айди");
                return null;
            } else {
                StoreHistory(returnTask);
                return returnTask;
            }
        }
    }

    // получение списка всех подзадач у эпика
    @Override
    public ArrayList<SubTask> showMeAllSubtaskInEpic(Epic epic) {
        return epic.subTasksList;
    }

    // возврат хешмап
    public HashMap<Long, Task> getHashMap() {
        return tasks;
    }

    // удаление ранее добавленных задач — всех
    @Override
    public void removeAll() throws ManagerSaveException {
        prioritizedList.clear();
        tasks.clear();
    }

    public void removeFromHistory(Task task) {
        inMemoryHistoryManager.remover(task.getId());
    }

    // удаление ранее добавленных задач, подзадач, эпиков по идентификатору
    @Override
    public void removeTaskById(long id) throws ManagerSaveException, IOException, InterruptedException {
        long index;
        counter++;
        saveCounter();

        if (tasks.get(id) != null) {
            removeFromHistory(tasks.get(id));
            prioritizedList.remove(tasks.get(id));
            tasks.remove(id);

        } else if (tasks.get(id) == null) {
            ArrayList<Task> TasksForOutput2 = showMeAllEpics();

            for (Task task : TasksForOutput2) {
                Epic x = (Epic) task;
                for (int y = 0; y < x.subTasksList.size(); y++) {
                    if (x.subTasksList.get(y).getId() == id) {
                        index = x.getId();
                        removeFromHistory(x.subTasksList.get(y));
                        x.subTasksList.remove(y);
                        checkEpicStatus((Epic) tasks.get(index));

                        prioritizedList.clear();
                        for (Task tasks : tasks.values()) {
                            prioritizedList.add(tasks);
                        }
                    }
                }
            }
        }
    }

}
