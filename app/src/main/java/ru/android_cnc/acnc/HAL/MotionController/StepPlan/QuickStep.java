package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

public class QuickStep {

    public final long time_;
    private byte bitSet;

    public QuickStep(long scaled_pos, byte x_mask, byte y_mask) {
        time_ = scaled_pos;
        bitSet = (byte)(x_mask|y_mask);
    }

    public byte getBitSet() {
        return bitSet;
    }

}
