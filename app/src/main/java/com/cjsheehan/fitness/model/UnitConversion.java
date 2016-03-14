package com.cjsheehan.fitness.model;

public class UnitConversion {
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
    private double STEPS_PER_METRE = 1;
    private double STEPS_PER_YARD = STEPS_PER_METRE ;
    private double STEPS_PER_KILOMETRE = 762;
    private double STEPS_PER_MILE = 2112;

    // PER STEP
    private double METRES_PER_STEP = 0.762; // COMMONLY USED FACTOR
    private double YARDS_PER_STEP = 1 / STEPS_PER_YARD;
    private double KILOMETRES_PER_STEP = 1 / STEPS_PER_KILOMETRE;
    private double MILES_PER_STEP = 1 / STEPS_PER_MILE;

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
                factor = YARDS_PER_STEP;
                break;
            case METRE:
                factor = KILOMETRES_PER_STEP;
                break;
            case KILOMETRE:
                factor = KILOMETRES_PER_STEP;
                break;
            case MILE:
                factor = MILES_PER_STEP;
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
        updateYardsPerStep();
        updateKilometresPerStep();
        updateMilesPerStep();
    }

    private void updateYardsPerStep() {
        YARDS_PER_STEP = METRES_PER_STEP * YARDS_PER_METRE;
    }

    private void updateKilometresPerStep() {
        KILOMETRES_PER_STEP = METRES_PER_STEP * KILOMETRES_PER_METRE;
    }

    private void updateMilesPerStep() {
        MILES_PER_STEP = METRES_PER_STEP * MILES_PER_METRE;
    }
}
