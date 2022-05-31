package com.example;

public class Rstatus {
    private int id;
    private String status;

    public Rstatus(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}