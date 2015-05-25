/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;

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

    public static boolean conform(MotionControllerCommand command1, MotionControllerCommand command2){
        if(command1 == null){
            // command2 is first motion in sequence
            command2.setVelocityPlan(getInitialPlan(command2));
            return true;
        } else {
            if(command1 == null){
                // command1 is last motion in sequence
            } else {
            }
        }
        return false;
    }

    private static VelocityPlan getInitialPlan(MotionControllerCommand command){
        return null;
    };
}
