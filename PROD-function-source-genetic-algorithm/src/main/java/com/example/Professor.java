package com.example;

import org.bson.types.ObjectId;

/**
 * Simple Professor abstraction.
 */
public class Professor {
    ObjectId _id;
    private int professorId;
    private final String lastName;
    int maxClasses;
    int assignedClasses = 0;
    String[] disciplines;

    /**
     * Initalize new Professor
     * 
     * @param professorId   The ID for this professor
     * @param professorName The name of this professor
     */
    public Professor(int professorId, String professorName, String[] disciplines) {
        this.professorId = professorId;
        this.lastName = professorName;
        this.disciplines = disciplines;
    }

    public Professor(ObjectId _id, String professorName, int maxClasses, String[] disciplines) {
        this._id = _id;
        this.lastName = professorName;
        this.maxClasses = maxClasses;
        this.disciplines = disciplines;
    }

    public Professor(ObjectId _id, String lastName, int maxClasses, int assignedClasses, String[] disciplines) {
        this._id = _id;
        this.lastName = lastName;
        this.maxClasses = maxClasses;
        this.assignedClasses = assignedClasses;
        this.disciplines = disciplines;
    }

    public Professor(int professorId, String professorName) {
        this.professorId = professorId;
        this.lastName = professorName;
    }

    /**
     * Get professorId
     * 
     * @return professorId
     */
    public int getProfessorId() {
        return this.professorId;
    }

    public String dString() {
        String str = "";
        for (int i = 0; i < this.disciplines.length; i++) {
            str += this.disciplines[i] + ", ";
        }
        return str;
    }

    public void called() {
        this.assignedClasses++;
    }

    public int getNumAssigned() {
        return this.assignedClasses;
    }

    /**
     * Get professor's name
     * 
     * @return professorName
     */
    public String getProfessorName() {
        return this.lastName;
    }

    public String[] getDisciplines() {
        return this.disciplines;
    }

    public void setJavaId(int id) {
        this.professorId = id;
    }

    public String idToString() {
        String st = this._id.toString();
        return st;
    }

    public int getMaxClasses() {
        return this.maxClasses;
    }

    public void setAssigned(int i) {
        this.assignedClasses = i;
    }

    public void print() {
        System.out.println(
                String.format("javaId: %d, id: %s, %s, max classes: %d, assigned: %d, disciplines: (%s)",
                        this.getProfessorId(),
                        this.idToString(), this.getProfessorName(),
                        this.getMaxClasses(), this.assignedClasses, this.dString()));
    }
}