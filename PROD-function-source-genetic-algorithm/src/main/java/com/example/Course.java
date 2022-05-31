package com.example;

import org.bson.types.ObjectId;

public class Course {
    ObjectId _id;
    int javaId;
    String courseNumber;
    String courseTitle;
    String[] disciplines;

    public Course(ObjectId _id, String courseNumber, String courseTitle, String[] disciplines) {
        this._id = _id;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.disciplines = disciplines;
    }
    
    public Course(int javaId, String courseNumber, String courseTitle, String[] disciplines) {
        this.javaId = javaId;
        this.courseNumber = courseNumber;
        this.courseTitle = courseTitle;
        this.disciplines = disciplines;
    }


    public ObjectId get_id() {
        return _id;
    }

    public int getJavaId() {
        return this.javaId;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseTitle() {
        return this.courseTitle;
    }

    public void setJavaId(int id) {
        this.javaId = id;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String[] getDisciplines() {
        return this.disciplines;
    }

    public void setDisciplines(String[] disciplines) {
        this.disciplines = disciplines;
    }

    public String idToString() {
        String st = this._id.toString();
        return st;
    }

    public String dString() {
        String str = "";
        for (int i = 0; i < this.disciplines.length; i++) {
            str += this.disciplines[i] + ", ";
        }
        return str;
    }

    public void print() {
        System.out.println(
                String.format("javaId: %d, id: %s, CRN: %s, %s, disciplines: (%s)", this.getJavaId(), this.idToString(),
                        this.getCourseNumber(),
                        this.getCourseTitle(), this.dString()));
    }

}
