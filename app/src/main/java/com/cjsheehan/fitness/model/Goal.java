package com.cjsheehan.fitness.model;

public class Goal {

    private String title;
    private int id = 0;
    private int target;
    private GoalState goalState;

    public Goal(String title, int id, int target, GoalState goalState) {

        if(target < 1) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        if(id < 0) throw new IllegalArgumentException("ERROR: ID must be greater than 0");
        this.title = title;
        this.id = id;
        this.target = target;
        this.goalState = goalState;
    }

    public Goal(String title, int target, GoalState goalState) {

        if(target < 1) throw new IllegalArgumentException("ERROR: Goal target should be more than 0");
        this.title = title;
        this.target = target;
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

    public GoalState getGoalState() {
        return goalState;
    }

    public void setGoalState(GoalState goalState) {
        this.goalState = goalState;
    }
}