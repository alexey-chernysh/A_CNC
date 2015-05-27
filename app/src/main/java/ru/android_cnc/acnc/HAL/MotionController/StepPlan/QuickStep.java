package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

public class QuickStep {

    public final long time;
    private byte bitSet;

    public QuickStep(long scaled_pos, StepPlanRecord x_step, StepPlanRecord y_step) {
        time = scaled_pos;
        bitSet = (byte)0b00000000;
        if(x_step != null){
            this.setBit(BitSet.STEP_X,x_step.getX().isStep());
            this.setBit(BitSet.DIR_X,x_step.getX().dirIsForward());
        }
        if(y_step != null){
            this.setBit(BitSet.STEP_Y,y_step.getY().isStep());
            this.setBit(BitSet.DIR_Y,y_step.getY().dirIsForward());
        }
    }

    public void setBit(BitSet b, boolean flag){
        if(flag) bitSet = (byte)(bitSet|b.mask);
        else bitSet = (byte)(bitSet&~b.mask);
    }

    public byte getBitSet() {
        return bitSet;
    }

}
