package com.astrapia.astrapia.models;

public class AttendanceSheet {
    private String id;
    private String name;
    private String date;
    private String scheduleId;
    private String teacherId;

    public AttendanceSheet(){}

    public AttendanceSheet(String id, String name, String date, String scheduleId, String teacherId){
        this.id = id;
        this.name = name;
        this.date = date;
        this.scheduleId = scheduleId;
        this.teacherId = teacherId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getScheduleId() {
        return scheduleId;
    }
}