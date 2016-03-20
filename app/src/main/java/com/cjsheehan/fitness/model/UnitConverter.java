package com.cjsheehan.fitness.model;

public class UnitConverter {

    // ABBREVIATION
    public static final String STEP = "step";
    public static final String YARD = "yd";
    public static final String METRE = "m";
    public static final String MILE = "mi";
    public static final String KILOMETRE = "km";

    // YARDS
    private static double YARD_TO_METRE = 0.9144;
    private static double YARD_TO_KILOMETRE = 0.0009144;
    private static double YARD_TO_MILE = 0.0005681818;
    private static double YARD_TO_STEP = 1.2;

    // METRES
    private static double METRE_TO_YARD = 1.0936133;
    private static double METRE_TO_KILOMETRE = 0.001;
    private static double METRE_TO_MILE = 0.000621371192;
    private static double METRE_TO_STEP = 1.31233595800525;

    // KILOMETRES
    private static double KILOMETRE_TO_YARD = 1093.6133;
    private static double KILOMETRE_TO_METRE = 1000;
    private static double KILOMETRE_TO_MILE = 0.621371192;
    private static double KILOMETRE_TO_STEP = 1312.33595801;

    // MILES
    private static double MILE_TO_YARD = 1760;
    private static double MILE_TO_METRE = 1609.344;
    private static double MILE_TO_KILOMETRE = 1.609344;
    private static double MILE_TO_STEP = 2112;

    // STEPS
    private static double STEP_TO_METRE = 1 / METRE_TO_STEP;
    private static double STEP_TO_KILOMETRE = 1 / KILOMETRE_TO_STEP;
    private static double STEP_TO_MILE = 1 / MILE_TO_STEP;
    private static double STEP_TO_YARD = 1 / YARD_TO_STEP;

    private static double STEP_TO_METRES = 0.762; // COMMONLY USED FACTOR;


    //public UnitConverter (double step_length_in_metres) {
    //    setMetresPerStep(step_length_in_metres);
    //}

    public static double convert(double input, Unit unitFrom, Unit unitTo) {
        double output = 0;
        switch (unitFrom) {
            case METRE:
                output = input * metresTo(unitTo);
                break;
            case KILOMETRE:
                output = input * kilometresTo(unitTo);
                break;
            case MILE:
                output = input * milesTo(unitTo);
                break;
            case YARD:
                output = input * yardsTo(unitTo);
                break;
            case STEP:
                output = input * stepsTo(unitTo);
                break;
        }
        return output;
    }



    public static double stepsTo(Unit unit) {
        double factor = 0;
        switch (unit) {
            case STEP:
                factor = 1;
                break;
            case YARD:
                factor = STEP_TO_YARD;
                break;
            case METRE:
                factor = STEP_TO_METRE;
                break;
            case KILOMETRE:
                factor = STEP_TO_KILOMETRE;
                break;
            case MILE:
                factor = STEP_TO_MILE;
                break;
        }
        return factor;
    }

    public static double yardsTo(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = YARD_TO_STEP;
                break;
            case YARD:
                factor = 1;
                break;
            case METRE:
                factor = YARD_TO_METRE;
                break;
            case KILOMETRE:
                factor = YARD_TO_KILOMETRE;
                break;
            case MILE:
                factor = YARD_TO_MILE;
                break;
        }
        return factor;
    }

    public static double metresTo(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = METRE_TO_STEP;
                break;
            case YARD:
                factor = METRE_TO_YARD;
                break;
            case METRE:
                factor = 1;
                break;
            case KILOMETRE:
                factor = METRE_TO_KILOMETRE;
                break;
            case MILE:
                factor = METRE_TO_MILE;
                break;
        }
        return factor;
    }

    public static double kilometresTo(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = KILOMETRE_TO_STEP;
                break;
            case YARD:
                factor = KILOMETRE_TO_YARD;
                break;
            case METRE:
                factor = KILOMETRE_TO_METRE;
                break;
            case KILOMETRE:
                factor = 1;
                break;
            case MILE:
                factor = KILOMETRE_TO_MILE;
                break;
        }
        return factor;
    }

    public static double milesTo(Unit convertTo) {
        double factor = 0;
        switch (convertTo) {
            case STEP:
                factor = MILE_TO_STEP;
                break;
            case YARD:
                factor = MILE_TO_YARD;
                break;
            case METRE:
                factor = MILE_TO_METRE;
                break;
            case KILOMETRE:
                factor = MILE_TO_KILOMETRE;
                break;
            case MILE:
                factor = 1;
                break;
        }
        return factor;
    }

    public static void setMetresPerStep(double factor) {
        if(factor <= 0)
            throw new IllegalArgumentException("metres per step factor must be > 0");
        STEP_TO_METRE = factor;
        METRE_TO_STEP = 1 / STEP_TO_METRE;
        updateYardsFactors();
        updateKilometresFactors();
        updateMilesFactors();
    }

    private static void updateYardsFactors() {
        STEP_TO_YARD = STEP_TO_METRE * METRE_TO_YARD;
        YARD_TO_STEP = 1 / STEP_TO_YARD;
    }

    private static void updateKilometresFactors() {
        STEP_TO_KILOMETRE = STEP_TO_METRE * METRE_TO_KILOMETRE;
        KILOMETRE_TO_STEP = 1 / STEP_TO_KILOMETRE;
    }

    private static void updateMilesFactors() {
        STEP_TO_MILE = STEP_TO_METRE * METRE_TO_MILE;
        MILE_TO_STEP = 1 / STEP_TO_MILE;
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
