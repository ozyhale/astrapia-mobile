package com.astrapia.astrapia.models;

public class StudentAttendance {
    private String id;
    private String attendanceSheetId;
    private String studentId;
    private String status;
    private String firstName;
    private String middleName;
    private String lastName;
    private String attendanceSheetDate;
    private String subjectId;
    private String subjectCode;
    private String subjectName;
    private AttendanceSheet attendanceSheet;

    public StudentAttendance(){}

    public StudentAttendance(String id, String attendanceSheetId, String studentId, String status, String firstName, String middleName, String lastName) {
        this.id = id;
        this.attendanceSheetId = attendanceSheetId;
        this.studentId = studentId;
        this.status = status;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttendanceSheetId(String attendanceSheetId) {
        this.attendanceSheetId = attendanceSheetId;
    }

    public void setAttendanceSheetDate(String attendanceSheetDate) {
        this.attendanceSheetDate = attendanceSheetDate;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setAttendanceSheet(AttendanceSheet attendanceSheet) {
        this.attendanceSheet = attendanceSheet;
    }

    public String getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStatus() {
        return status;
    }

    public String getAttendanceSheetId() {
        return attendanceSheetId;
    }

    public String getAttendanceSheetDate() {
        return attendanceSheetDate;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public AttendanceSheet getAttendanceSheet() {
        return attendanceSheet;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(int mode){
        if(mode == Student.MODE_LAST_NAME_FIRST){
            return lastName + ", " + firstName + " " + middleName + " ";
        }else if(mode == Student.MODE_MIDDLE_INITIAL){
            return firstName + " " + middleName.charAt(0) + ". " + lastName;
        }else{
            return firstName + " " + middleName + " " + lastName;
        }
    }

    public String getFullName(){
        return getFullName(Student.MODE_FIRST_NAME_FIRST);
    }
}
