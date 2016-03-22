package com.cjsheehan.fitness.model;

import java.io.Serializable;

public class Goal implements Serializable {

    private int id = 0;
    private String title;
    private double target;
    private double progress;
    private Unit unit;
    private ActiveState _activeState;
    private String date;


    public Goal(String title, String date, int id, double progress, double target, Unit unit, ActiveState _activeState) {

        if(target <= 0) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        if(id < 0) throw new IllegalArgumentException("ERROR: ID must be greater than 0");
        this.title = title;
        this.date = date;
        this.id = id;
        this.target = target;
        this.progress = progress;
        this.unit = unit;
        this._activeState = _activeState;
    }

    public Goal(String title, String date, double target, double progress, Unit unit, ActiveState _activeState) {
        if(target <= 0) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        this.title = title;
        this.date = date;
        this.target = target;
        this.progress = progress;
        this.unit = unit;
        this._activeState = _activeState;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActiveState getActiveState() {
        return _activeState;
    }

    public void setActiveState(ActiveState activeState) {
        this._activeState = activeState;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public static Goal copy(Goal goal) {
        return new Goal(goal.getTitle(), goal.getDate(), goal.getTarget(), goal.getProgress(), goal.getUnit(), goal.getActiveState());
    }
}