/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

public class VelocityPlan {

    public static final VelocityPlanMode mode = VelocityPlanMode.CONSTANT_VELOCITY;

    private double requiredVel_;

    public VelocityPlan(double v){
        requiredVel_ = v;
    }

    public double getValue(double phase){ return requiredVel_; }

    public enum VelocityPlanMode{
        CONSTANT_VELOCITY,
        TRAPEZOIDAL,
        S_CURVE
    }
}
