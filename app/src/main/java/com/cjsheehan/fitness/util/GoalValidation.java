package com.cjsheehan.fitness.util;

public class GoalValidation {

    public static GoalValidationCode checkInput(String title, String target) {
        if(title.isEmpty()) return GoalValidationCode.FAIL_TITLE_IS_EMPTY;
        if(target.isEmpty()) return GoalValidationCode.FAIL_TARGET_IS_EMPTY;
        double targetDistance;
        try {
            targetDistance = Double.parseDouble(target);
            if(targetDistance <= 0) {
                return GoalValidationCode.FAIL_TARGET_LTE0;
            }
        }
        catch(NumberFormatException nfe) {
            return GoalValidationCode.FAIL_TARGET_NAN;
        }
        return GoalValidationCode.OK;
    }
}
