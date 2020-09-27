package com.nazmusashrafi.multi_timer_2_master;

import java.io.Serializable;

public class SingleTimer implements Serializable {
    private int stepNumber;
    private String title;
    private String description;
    private int time;
    private String color;

    public SingleTimer(int stepNumber, String title, String description, int time, String color) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description;
        this.time = time;
        this.color = color;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
