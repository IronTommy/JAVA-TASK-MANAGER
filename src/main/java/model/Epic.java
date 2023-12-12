package model;

import service.auxiliary.Status;

import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {
    public ArrayList<SubTask> subTasksList;

    public Epic(String task, String description) {
        super(task, description);
        subTasksList = new ArrayList();
    }

    public Epic(String task, String description, String durations, String startTime) {
        super(task, description, durations, startTime);
        subTasksList = new ArrayList();
    }

    public Epic(String task, String description, Status status, long id, String durations, String startTime) {
        super(task, description, status, id, durations, startTime);
        subTasksList = new ArrayList();
    }

    public Epic(String task, String description, long id, String durations, String startTime) {
        super(task, description, id, durations, startTime);
        subTasksList = new ArrayList();
    }

    public Epic(String task, String description, long id) {
        super(task, description, id);
        subTasksList = new ArrayList();
    }

    public Epic() {
        super();
    }

    public void setSubTasksList(ArrayList<SubTask> subTasksList) {
        this.subTasksList = subTasksList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name ='" + getName() +
                ", description ='" + getDescription() +
                ", id =" + getId() +
                ", status =" + getStatus() +
                ", duration(h) = " + getDuration().toHours() +
                ", start_time ='" + getStartTime().format(formatter) +
                ", end_time ='" + getEndTime().format(formatter) +
                ", subTasksList ='" + Arrays.toString(new ArrayList[]{subTasksList}) + '\'' +
                '}';
    }
}