package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

/**
 * Created by alexey on 18.05.15.
 */

public class Step {

    private boolean step;
    private boolean dir;

    public Step(boolean s, boolean d){
        step = s;
        dir = d;
    }

    public boolean dirIsForward() {
        return dir;
    }

    public boolean dirIsBackward(){
        return !dir;
    }

    public boolean isStep() {
        return step;
    }

}
