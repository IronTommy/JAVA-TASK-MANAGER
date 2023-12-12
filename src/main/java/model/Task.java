package model;

import service.auxiliary.Status;
import service.inMemory.InMemoryTasksManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private long id;
    private String name;
    private String description;
    private Status status = Status.NEW;
    private Duration duration = Duration.ofMinutes(0);
    private LocalDateTime startTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
    private LocalDateTime endTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" yyyy-MMM-dd HH:mm");

    public Task() {
    }

    public Task(String task, String description) {
        this.name = task;
        this.description = description;
        this.id = InMemoryTasksManager.idGenerator();
    }

    public Task(String task, Status status) {
        this.name = task;
        this.status = status;
        this.id = InMemoryTasksManager.idGenerator();
    }

    public Task(String task, String description, long id) {
        this.name = task;
        this.description = description;
        this.id = id;
    }

    public Task(String task, Status status, long id) {
        this.name = task;
        this.status = status;
        this.id = id;
    }

    public Task(String task, String description, Status status) {
        this.name = task;
        this.description = description;
        this.status = status;
        this.id = InMemoryTasksManager.idGenerator();
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(String task, String description, Status status, String durations, String startTime) {
        this.name = task;
        this.description = description;
        this.status = status;
        this.id = InMemoryTasksManager.idGenerator();
        this.duration = Duration.parse(durations);
        this.startTime = LocalDateTime.parse(startTime);
        this.endTime = LocalDateTime.parse(startTime).plus(Duration.parse(durations));

    }

    public Task(String task, String description, Status status, long id, String durations, String startTime) {
        InMemoryTasksManager.idGenerator();
        this.name = task;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = Duration.parse(durations);
        this.startTime = LocalDateTime.parse(startTime);
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(String task, Status status, String durations, String startTime) {
        this.name = task;
        this.status = status;
        this.id = InMemoryTasksManager.idGenerator();
        this.duration = Duration.parse(durations);
        this.startTime = LocalDateTime.parse(startTime);
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(String task, String description, String durations, String startTime) {
        this.name = task;
        this.description = description;
        this.id = InMemoryTasksManager.idGenerator();
        this.duration = Duration.parse(durations);
        this.startTime = LocalDateTime.parse(startTime);
        this.endTime = this.startTime.plus(this.duration);
    }

    public Task(String task, String description, Status status, long id) {
        this.name = task;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String task, String description, long id, String durations, String startTime) {
        this.name = task;
        this.description = description;
        this.id = id;
        this.duration = Duration.parse(durations);
        this.startTime = LocalDateTime.parse(startTime);
        this.endTime = this.startTime.plus(this.duration);

    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name ='" + name +
                ", description ='" + description +
                ", id =" + id +
                ", status ='" + status +
                ", duration(h) = " + duration.toHours() +
                ", start_time ='" + startTime.format(formatter) +
                ", end_time ='" + endTime.format(formatter) + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object compareObject) {
        if (this == compareObject) return true;
        if (compareObject == null) return false;
        if (getClass() != compareObject.getClass()) return false;
        Task object = (Task) compareObject;
        return id == object.id && name.equals(object.name);
    }

}