/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController.VelocityPlan;

import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerService;
import ru.android_cnc.acnc.HAL.MotionController.StepPlan.StepPlan;
import ru.android_cnc.acnc.Interpreter.Exceptions.ExecutionException;

public class VelocityPlan {

    public static final double velocityTol = 0.01;

    public VelocityPlan(MotionControllerCommand command) throws ExecutionException {
        double l = command.length();
        double feedRate = command.getFeedRate();

        final double step_x = MotionControllerService.getX_mm_in_step();
        final double step_y = MotionControllerService.getY_mm_in_step();
        final double timeScale = MotionControllerService.getTikInMM();

        StepPlan stepPlan = new StepPlan(command);


    }

    public static double conform(MotionControllerCommand command1, MotionControllerCommand command2) throws ExecutionException {
        if(command1 == null){
            // command2 is first motion in sequence
            command2.setVelocityPlan(new VelocityPlan(command2));
            return 1.0;
        } else {
            if(command1 == null){
                // command1 is last motion in sequence
            } else {
            }
        }
        return 1.0;
    }

}
