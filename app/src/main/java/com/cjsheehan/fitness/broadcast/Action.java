package com.cjsheehan.fitness.broadcast;

/**
 * Created by Chris on 10/03/2016.
 */
public enum Action {
    ADD_GOAL(1, "addGoal"),
    REMOVE_GOAL(2, "removeGoal"),
    UPDATE_GOAL(3, "updateGoal");

    public int id;
    public String title;

    Action(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static Action getById(int id) {
        for (Action action : Action.values())
            if (id == action.id)
                return action;
        return null;
    }
}
