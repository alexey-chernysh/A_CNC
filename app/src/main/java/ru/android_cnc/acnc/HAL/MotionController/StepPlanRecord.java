package ru.android_cnc.acnc.HAL.MotionController;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlanRecord {

    private double position;
    private StepTag tag;

    public StepPlanRecord(double p, StepTag t){
        position = p;
        tag = t;
    }

    public StepPlanRecord(StepTag t){
        position = Double.MAX_VALUE;
        tag = t;
    }

    public double getPosition() {
        return position;
    }

    public enum StepTag {
        X,
        Y,
        X_N_Y;

        public boolean isX() {
            if(this == X) return true;
            if(this == X_N_Y) return true;
            return false;
        }

        public boolean isY() {
            if(this == Y) return true;
            if(this == X_N_Y) return true;
            return false;
        }
    }
}
