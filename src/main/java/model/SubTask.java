package model;

import service.auxiliary.Status;

public class SubTask extends Task {
    private long epicId;

    public SubTask() {
    }

    public SubTask(String task, Status status, long Epicid, String durations, String startTime) {
        super(task, status, durations, startTime);
        setStatus(status);
        setEpicId(Epicid);
    }

    public SubTask(String task, Status status, long Epicid, long id, String durations, String startTime) {
        super(task, status, durations, startTime);
        setStatus(status);
        setEpicId(Epicid);
        setId(id);
    }

    public void setEpicId(long epicid) {
        this.epicId = epicid;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask {" +
                "name ='" + getName() +
                ", status =" + getStatus() +
                ", id =" + getId() +
                ", epic_id =" + epicId +
                ", duration(h) = " + getDuration().toHours() +
                ", start_time ='" + getStartTime().format(formatter) +
                ", end_time ='" + getEndTime().format(formatter) + '\'' +
                '}';
    }

}