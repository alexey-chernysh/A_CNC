package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

public enum BitSet {
    STEP_X(0),
    DIR_X(1),
    STEP_Y(2),
    DIR_Y(3);

    public final byte mask;
    public boolean is(BitSet check){
        return (this.mask&check.mask)!=0;
    }

    BitSet(int bitNum){
        mask = (byte)(0b10000000 >>> bitNum);
    }
}
