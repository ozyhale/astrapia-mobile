package com.astrapia.astrapia.models;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String schoolId;
    private String parentId;
    private List<StudentSchedule> studentSchedules = new ArrayList<>();

    public static final int MODE_FIRST_NAME_FIRST = 0;
    public static final int MODE_LAST_NAME_FIRST = 1;
    public static final int MODE_MIDDLE_INITIAL = 2;

    public Student(){}

    public Student(String id, String firstName, String middleName, String lastName, String schoolId, String parentId) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.schoolId = schoolId;
        this.parentId = parentId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setStudentSchedules(List<StudentSchedule> studentSchedules) {
        this.studentSchedules = studentSchedules;
    }

    public void addStudentSchedule(StudentSchedule studentSchedule){
        studentSchedules.add(studentSchedule);
    }

    public String getId() {
        return id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getParentId() {
        return parentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public List<StudentSchedule> getStudentSchedules() {
        return studentSchedules;
    }

    public String getFullName(int mode){
        if(mode == MODE_LAST_NAME_FIRST){
            return lastName + ", " + firstName + " " + middleName + " ";
        }else if(mode == MODE_MIDDLE_INITIAL){
            return firstName + " " + middleName.charAt(0) + ". " + lastName;
        }else{
            return firstName + " " + middleName + " " + lastName;
        }
    }

    public String getFullName(){
        return getFullName(MODE_FIRST_NAME_FIRST);
    }
}
