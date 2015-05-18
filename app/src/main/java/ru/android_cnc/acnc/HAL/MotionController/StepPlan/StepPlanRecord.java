package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlanRecord {

    private double position;

    private Step x_;
    private Step y_;

    public StepPlanRecord(double p, Step x, Step y){
        position = p;
    }

    public double getPosition() {
        return position;
    }

    public Step getX() {
        return x_;
    }

    public Step getY() {
        return y_;
    }

}
