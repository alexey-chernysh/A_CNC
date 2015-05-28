package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlanRecord {

    private double position;
    private byte bitArray = 0b00000000;

    public StepPlanRecord(double p, boolean StepX, boolean DirX, boolean StepY, boolean DirY){
        if(StepX) bitArray = (byte)(bitArray|BitSet.STEP_X.mask);
        if(DirX)  bitArray = (byte)(bitArray|BitSet.DIR_X.mask);
        if(StepY) bitArray = (byte)(bitArray|BitSet.STEP_Y.mask);
        if(DirY)  bitArray = (byte)(bitArray|BitSet.DIR_Y.mask);
        position = p;
    }

    public double getPosition() {
        return position;
    }

    public boolean isStepX() {
        return (bitArray|BitSet.STEP_X.mask)!=0;
    }

    public boolean isDirX() {
        return (bitArray|BitSet.DIR_X.mask)!=0;
    }

    public boolean isStepY() {
        return (bitArray|BitSet.STEP_Y.mask)!=0;
    }

    public boolean isDirY() {
        return (bitArray|BitSet.DIR_Y.mask)!=0;
    }

    public byte getBitArray() {
        return bitArray;
    }

}
