package com.cjsheehan.fitness.model;

import android.security.keystore.UserNotAuthenticatedException;

public class UnitConversion {

    // ABBREVIATION
    public final static String STEP = "step";
    public final static String YARD = "yd";
    public final static String METRE = "m";
    public final static String MILE = "mi";
    public final static String KILOMETRE = "km";

    // YARDS
    private static final double YARDS_PER_METRE = 1.09361;
    private static final double YARDS_PER_KILOMETRE = 1093.61;
    private static final double YARDS_PER_MILE = 1760;

    // METRES
    private static final double METRES_PER_YARD = 0.9144;
    private static final double METRES_PER_KILOMETRE = 1000;
    private static final double METRES_PER_MILE = 1609.344;

    // KILOMETRES
    private static final double KILOMETRES_PER_YARD = 0.0009144;
    private static final double KILOMETRES_PER_METRE = 0.001;
    private static final double KILOMETRE_PER_MILE = 1.60934;

    // MILES
    private static final double MILES_PER_YARD = 1 / YARDS_PER_MILE;
    private static final double MILES_PER_METRE = 1 / METRES_PER_MILE;
    private static final double MILES_PER_KILOMETRE = 1 / KILOMETRE_PER_MILE;

    // STEPS
    private double STEPS_PER_METRE = 0.762; // COMMONLY USED FACTOR;
    private double METRES_PER_STEP = 1 / STEPS_PER_METRE;

    private double YARDS_PER_STEP = METRES_PER_STEP * YARDS_PER_METRE;
    private double STEPS_PER_YARD = 1 / YARDS_PER_STEP;

    private double KILOMETRES_PER_STEP = METRES_PER_STEP * KILOMETRES_PER_METRE;
    private double STEPS_PER_KILOMETRE = 1 / KILOMETRES_PER_STEP;

    private double MILES_PER_STEP = METRES_PER_STEP * MILES_PER_METRE;
    private double STEPS_PER_MILE = 1 / MILES_PER_STEP;

    public UnitConversion (double metresPerStep) {
        setMetresPerStep(metresPerStep);
    }

    public double convert(double input, Unit unitFrom, Unit unitTo) {
        double output = 0;
        switch (unitFrom) {
            case METRE:
                output = input / metresPer(unitTo);
                break;
            case KILOMETRE:
                output = input / kilometresPer(unitTo);
                break;
            case MILE:
                output = input / milesPer(unitTo);
                break;
            case YARD:
                output = input / yardsPer(unitTo);
                break;
            case STEP:
                output = input / stepsPer(unitTo);
                break;
        }
        return output;
    }



    public double stepsPer(Unit unit) {
        double factor = 0;
        switch (unit) {
            case STEP:
                factor = 1;
                break;
            case YARD:
                factor = STEPS_PER_YARD;
                break;
            case METRE:
                factor = STEPS_PER_METRE;
                break;
            case KILOMETRE:
                factor = STEPS_PER_KILOMETRE;
                break;
            case MILE:
                factor = STEPS_PER_MILE;
                break;
        }
        return factor;
    }

    public double yardsPer(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = YARDS_PER_STEP;
                break;
            case YARD:
                factor = 1;
                break;
            case METRE:
                factor = YARDS_PER_METRE;
                break;
            case KILOMETRE:
                factor = YARDS_PER_KILOMETRE;
                break;
            case MILE:
                factor = YARDS_PER_MILE;
                break;
        }
        return factor;
    }

    public double metresPer(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = METRES_PER_STEP;
                break;
            case YARD:
                factor = METRES_PER_YARD;
                break;
            case METRE:
                factor = 1;
                break;
            case KILOMETRE:
                factor = METRES_PER_KILOMETRE;
                break;
            case MILE:
                factor = METRES_PER_MILE;
            break;
        }
        return factor;
    }

    public double kilometresPer(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = KILOMETRES_PER_STEP;
                break;
            case YARD:
                factor = KILOMETRES_PER_YARD;
                break;
            case METRE:
                factor = KILOMETRES_PER_METRE;
                break;
            case KILOMETRE:
                factor = 1;
                break;
            case MILE:
                factor = KILOMETRE_PER_MILE;
                break;
        }
        return factor;
    }

    public double milesPer(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = MILES_PER_STEP;
                break;
            case YARD:
                factor = MILES_PER_YARD;
                break;
            case METRE:
                factor = MILES_PER_METRE;
                break;
            case KILOMETRE:
                factor = MILES_PER_KILOMETRE;
                break;
            case MILE:
                factor = 1;
                break;
        }
        return factor;
    }

    public void setMetresPerStep(double factor) {
        if(factor <= 0)
            throw new IllegalArgumentException("metres per step factor must be > 0");
        STEPS_PER_METRE = factor;
        METRES_PER_STEP = 1 / STEPS_PER_METRE;
        updateYardsFactors();
        updateKilometresFactors();
        updateMilesFactors();
    }

    private void updateYardsFactors() {
        YARDS_PER_STEP = METRES_PER_STEP * YARDS_PER_METRE;
        STEPS_PER_YARD = 1 / YARDS_PER_STEP;
    }

    private void updateKilometresFactors() {
        KILOMETRES_PER_STEP = METRES_PER_STEP * KILOMETRES_PER_METRE;
        STEPS_PER_KILOMETRE = 1 / KILOMETRES_PER_STEP;
    }

    private void updateMilesFactors() {
        MILES_PER_STEP = METRES_PER_STEP * MILES_PER_METRE;
        STEPS_PER_MILE = 1 / MILES_PER_STEP;
    }



    public static String toString(Unit unit) {
        String strUnit = null;
        switch (unit) {
            case METRE:
                strUnit = "m";
                break;
            case KILOMETRE:
                strUnit = "km";
                break;
            case MILE:
                strUnit ="mi";
                break;
            case YARD:
                strUnit ="yd";
                break;
            case STEP:
                strUnit ="step";
                break;
        }
        return strUnit;
    }

    public static Unit toUnit(String strUnit) {
        Unit unit = Unit.STEP;
        switch(strUnit) {
            case METRE:
                unit = Unit.METRE;
                break;
            case KILOMETRE :
                unit = Unit.KILOMETRE;
                break;
            case MILE:
                unit = Unit.MILE;
                break;
            case YARD:
                unit = Unit.YARD;
                break;
            case STEP:
                unit = Unit.STEP;
                break;
        }
        return unit;
    }
}
