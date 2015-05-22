/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerService;

public class VelocityPlan {

    public static final VelocityPlanMode mode = VelocityPlanMode.CONSTANT_VELOCITY;

	private double endVel_ = 0.0;
    private double requiredVel_;

    public VelocityPlan(double v){
        requiredVel_ = v;
    }

    public double getStartVel() { return startVel_;	}

	public double getEndVel() {	return endVel_;	}

    public void setStartVel(double v) {
        this.startVel_ = v;
    }

    public void setRequiredVel(double v) {
        this.requiredVel_ = v;
    }

    public void setEndVel(double v) {
        this.endVel_ = v;
    }

    private double startVel_ = 0.0;
    public enum VelocityPlanMode{
        CONSTANT_VELOCITY,
        TRAPEZOIDAL,
        S_CURVE
    }

    public enum MotionMatchingResult {
        OK,
        FAULT
    }
}
