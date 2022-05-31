package com.example;

import java.util.Arrays;

public class Timeslot {
    private final int timeslotId;
    private final String timeslot;

    // hold scheduled time info
    public int[] m;
    public int[] t;
    public int[] w;
    public int[] th;
    public int[] f;

    public int[] avoid;

    /**
     * Initalize new Timeslot
     * 
     * @param timeslotId The ID for this timeslot
     * @param timeslot   The timeslot being initalized
     */
    public Timeslot(int timeslotId, String timeslot) {
        this.timeslotId = timeslotId;
        this.timeslot = timeslot;
    }

    public Timeslot(int timeslotId, String timeslot, int[] m, int[] t, int[] w, int[] th, int[] f) {
        this.timeslotId = timeslotId;
        this.timeslot = timeslot;
        this.m = m;
        this.t = t;
        this.w = w;
        this.th = th;
        this.f = f;
    }

    /**
     * Returns the timeslotId
     * 
     * @return timeslotId
     */
    public int getTimeslotId() {
        return this.timeslotId;
    }

    /**
     * Returns the timeslot
     * 
     * @return timeslot
     */
    public String getTimeslot() {
        return this.timeslot;
    }

    public void addAvoid(int a) {
        if (avoid != null) {
            int length = avoid.length;
            this.avoid = Arrays.copyOf(this.avoid, length + 1);
            avoid[length] = a;
        } else {
            this.avoid = new int[] { a };
        }

    }

    public void printAvoid() {
        String ret = "";
        if (avoid != null) {
            for (int i = 0; i < this.avoid.length; i++) {
                ret += String.format("%d, ", this.avoid[i]);
            }
        }
        System.out.println(ret);
    }
}
