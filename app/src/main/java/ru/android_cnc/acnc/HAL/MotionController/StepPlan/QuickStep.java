package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

/**
 * Created by alexey on 25.05.15.
 */

public class QuickStep {

    public long time;
    private byte bitSet;

    public QuickStep(double normalized_length, StepPlanRecord x_step, StepPlanRecord y_step) {
        this.bitSet = bitSet;
    }

    public enum Bit{
        STEP_X,
        DIR_X,
        STEP_Y,
        DIR_Y;
    }

    public void setBit(Bit b, boolean val){

    }

    public byte getBitSet() {
        return bitSet;
    }

}
