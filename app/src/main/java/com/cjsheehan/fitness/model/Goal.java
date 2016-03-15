package com.cjsheehan.fitness.model;

import java.io.Serializable;

public class Goal implements Serializable {

    private int id = 0;
    private String title;
    private String date;
    private int target;
    private int progress;
    private Unit unit;
    private ActiveState goalState;

    private void setUnit(Unit unit) {
        this.unit = unit;
    }
    public Goal(String title, int id, int progress, int target, Unit unit, ActiveState goalState) {

        if(target < 1) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        if(id < 0) throw new IllegalArgumentException("ERROR: ID must be greater than 0");
        this.title = title;
        this.id = id;
        this.target = target;
        this.progress = progress;
        this.unit = unit;
        this.goalState = goalState;
    }

    public Goal(String title, int target, int progress, Unit unit, ActiveState goalState) {
        if(target < 1) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        this.title = title;
        this.id = id;
        this.target = target;
        this.progress = progress;
        this.unit = unit;
        this.goalState = goalState;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActiveState getGoalState() {
        return goalState;
    }

    public void setGoalState(ActiveState goalState) {
        this.goalState = goalState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Unit getUnit() {
        return unit;
    }
}