package com.nazmusashrafi.multi_timer_2_master;

import android.text.Editable;

import java.io.Serializable;

public class SingleTimer implements Serializable {
    private int stepNumber;
    private String title;
    private String description;
    private long time;
    private int color;

    public SingleTimer(int stepNumber, String title, String description, long time, int color) {
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
