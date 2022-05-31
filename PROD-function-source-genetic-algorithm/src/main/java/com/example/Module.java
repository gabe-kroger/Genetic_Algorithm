package com.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Simple course module abstraction, which defines the Professors teaching the
 * module.
 */
public class Module {
    private final int moduleId;
    private final String moduleCode;
    private final String module;
    private final int professorIds[];

    /**
     * Initialize new Module
     * 
     * @param moduleId
     * @param moduleCode
     * @param module
     * @param professorIds
     */
    public Module(int moduleId, String moduleCode, String module, int professorIds[]) {
        this.moduleId = moduleId;
        this.moduleCode = moduleCode;
        this.module = module;
        this.professorIds = professorIds;
    }

    /**
     * Get moduleId
     * 
     * @return moduleId
     */
    public int getModuleId() {
        return this.moduleId;
    }

    /**
     * Get module code
     * 
     * @return moduleCode
     */
    public String getModuleCode() {
        return this.moduleCode;
    }

    /**
     * Get module name
     * 
     * @return moduleName
     */
    public String getModuleName() {
        return this.module;
    }

    public String dString() {
        String str = "";
        for (int i = 0; i < this.professorIds.length; i++) {
            str += this.professorIds[i] + ", ";
        }
        return str;
    }

    /**
     * Get random professor Id
     * 
     * @return professorId
     */
    public int getRandomProfessorId(Timetable timetable) {
        // filter out professor ids of professors who have got to max classes
        int idx = 0;
        int[] temp = new int[professorIds.length];
        HashMap<Integer, Professor> professorSch = timetable.getProfessors();
        for (int i = 0; i < professorIds.length; i++) {
            Professor current = professorSch.get(professorIds[i]);
            if (!(current.getNumAssigned() > current.getMaxClasses())) {
                temp[idx] = professorIds[i];
                idx++;
            }
        }

        temp = Arrays.copyOf(temp, idx);

        if (temp.length == 0) {
            int rnd = new Random().nextInt(professorIds.length);
            // int professorId = professorIds[(int) (professorIds.length * Math.random())];
            return professorIds[rnd];
        }
        if (temp.length == 1) {
            return temp[0];
        } else {
            int rnd = new Random().nextInt(temp.length);
            // int professorId = professorIds[(int) (professorIds.length * Math.random())];
            return temp[rnd];
        }

    }

    public void print() {
        System.out.println(String.format("id: %d, code: %s, module: %s, professorIds: %s", this.moduleId,
                this.moduleCode, this.module, this.dString()));
        ;
    }
}
