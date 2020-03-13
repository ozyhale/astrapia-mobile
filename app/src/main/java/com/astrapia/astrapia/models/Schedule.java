package com.astrapia.astrapia.models;

public class Schedule {
    private String id, code, name, startTime, endTime, teacherId, subjectId, days;
    private Subject subject;

    public Schedule(){}

    public Schedule(String id, String code, String name, String startTime, String endTime, String teacherId, String subjectId, String days) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.days = days;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getDays() {
        return days;
    }

    public Subject getSubject() {
        return subject;
    }
}
