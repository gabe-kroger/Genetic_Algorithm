package com.example;

public class Schedule {
    public int classID;
    public String crn;
    public String courseTitle;
    public String instructor;
    public String scheduledTime;

    public Schedule(int classID, String crn, String courseTitle, String instructor, String scheduledTime) {
        this.classID = classID;
        this.crn = crn;
        this.courseTitle = courseTitle;
        this.instructor = instructor;
        this.scheduledTime = scheduledTime;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public void print() {
        System.out.println(String.format("Id: %d, crn: %s, instructor: %s, time: %s", this.classID, this.crn,
                this.instructor, this.scheduledTime));
    }

}

/*
 * doc.append("classID", classIndex);
 * doc.append("crn",
 * timetable.getModule(classes[i].getModuleId()).getModuleCode());
 * doc.append("courseTitle",
 * timetable.getModule(classes[i].getModuleId()).getModuleName());
 * doc.append("instructor",
 * timetable.getProfessor(classes[i].getProfessorId()).getProfessorName());
 * doc.append("scheduledTime",
 * timetable.getTimeslot(classes[i].getTimeslotId()).getTimeslot());
 */