package com.astrapia.astrapia.models;

public class StudentSchedule {
    private String id;
    private String studentId;
    private String scheduleId;
    private Schedule schedule;

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
