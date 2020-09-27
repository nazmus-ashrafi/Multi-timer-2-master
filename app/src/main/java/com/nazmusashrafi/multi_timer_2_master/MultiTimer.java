package com.nazmusashrafi.multi_timer_2_master;

import java.util.ArrayList;

public class MultiTimer {

    private int totalSteps;
    private String title;
    private int totalTime;
    private ArrayList<SingleTimer> singleTimerArrayList;
    private String id;

    public MultiTimer() {

    }

    public MultiTimer(int totalSteps, String title, int totalTime, ArrayList<SingleTimer> singleTimerArrayList) {
        this.totalSteps = totalSteps;
        this.title = title;
        this.totalTime = totalTime;
        this.singleTimerArrayList = singleTimerArrayList;
        this.id = id;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public ArrayList<SingleTimer> getSingleTimerArrayList() {
        return singleTimerArrayList;
    }

    public void setSingleTimerArrayList(ArrayList<SingleTimer> singleTimerArrayList) {
        this.singleTimerArrayList = singleTimerArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
