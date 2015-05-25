package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

/**
 * Created by alexey on 25.05.15.
 */

public class QuickStep {
    public long time;
    private byte bit_set;

    public enum Bit{
        STEP_X,
        DIR_X,
        STEP_Y,
        DIR_Y;
    }

    public void setBit(Bit b, boolean val){

    }
}
