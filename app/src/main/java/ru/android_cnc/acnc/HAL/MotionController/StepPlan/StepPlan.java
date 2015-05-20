package ru.android_cnc.acnc.HAL.MotionController.StepPlan;

import java.util.ArrayList;
import java.util.Iterator;

import ru.android_cnc.acnc.HAL.MotionController.ArcDirection;
import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerService;
import ru.android_cnc.acnc.Interpreter.Exceptions.ExecutionException;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlan {

    private ArrayList<StepPlanRecord> plan;

    public StepPlan(MotionControllerCommand command) throws ExecutionException {

        plan = new ArrayList();
        ArrayList<StepPlanRecord> planX = new ArrayList();
        ArrayList<StepPlanRecord> planY = new ArrayList();

        final double step_x = MotionControllerService.getX_mm_in_step();
        final double step_y = MotionControllerService.getY_mm_in_step();

        final double length = command.length();

        final double dx = command.getDX();
        final double dy = command.getDY();

        if (command instanceof CCommandStraightLine) {

            Step sx = new Step(true, (dx >= 0.0));
            Step sy = new Step(true, (dy >= 0.0));

            // generate x steps positions
            int n_x = (int) Math.abs(dx / step_x);
            if (n_x > 0) {
                final double dl = length / (n_x + 1.0);
                planX.add(new StepPlanRecord(0, new Step(false, (dx >= 0.0)), null));
                for (int i = 0; i < n_x; i++) {
                    double step_pos = dl / 2.0 + i * dl;
                    planX.add(new StepPlanRecord(step_pos, sx, null));
                }
                planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));
            }

            // generate y steps positions
            int n_y = (int) Math.abs(dy / step_y);
            if (n_y > 0) {
                final double dl = length / (n_y + 1.0);
                planY.add(new StepPlanRecord(0, null, new Step(false, (dy >= 0.0))));
                for (int i = 0; i < n_y; i++) {
                    double step_pos = dl / 2.0 + i * dl;
                    planY.add(new StepPlanRecord(step_pos, null, sy));
                }
                planY.add(new StepPlanRecord(length, null, new Step(false, (dy >= 0.0))));
            }
        }
        ;

        if (command instanceof CCommandArcLine) {

            CCommandArcLine arcCommand = (CCommandArcLine) command;
            ArcDirection arcDirection = arcCommand.getArcDirection();
            double radius = arcCommand.radius();
            double angle = arcCommand.angle();
            double startAngle = arcCommand.getStartRadialAngle();
            double endAngle = arcCommand.getEndRadialAngle();

            planX.add(new StepPlanRecord(0, new Step(false, (dx >= 0.0)), null));
            if(arcDirection == ArcDirection.COUNTERCLOCKWISE){
                // generate x steps positions
                double a = startAngle;
                double x = arcCommand.getStart().getX() - arcCommand.getCenter().getX();
                while(a < endAngle){
                    double shift = step_x;
                    if(a > 0.0) shift = - step_x;
                    double next_x = x + shift;
                    if()
                }

            } else {

            }
            planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));
        }
        ;

        // merge x & y arrays
        Iterator<StepPlanRecord> iteratorX = planX.iterator();
        StepPlanRecord nextX = null;
        if (iteratorX.hasNext()) nextX = iteratorX.next();

        Iterator<StepPlanRecord> iteratorY = planY.iterator();
        StepPlanRecord nextY = null;
        if (iteratorY.hasNext()) nextY = iteratorY.next();

        while ((nextX != null) | (nextY != null)) {

            double posX = Double.MAX_VALUE;
            if (nextX != null) posX = nextX.getPosition();

            double posY = Double.MAX_VALUE;
            if (nextY != null) posY = nextY.getPosition();

            if (posX < posY) {
                // x pulse first
                plan.add(nextX);
                if (iteratorX.hasNext()) nextX = iteratorX.next();
                else nextX = null;
            } else {
                if (posX > posY) {
                    // y pulse first
                    plan.add(nextY);
                    if (iteratorY.hasNext()) nextY = iteratorY.next();
                    else nextY = null;
                } else {
                    // both pulses in sync
                    plan.add(new StepPlanRecord(nextX.getPosition(), nextX.getX(), nextY.getY()));
                    if (iteratorX.hasNext()) nextX = iteratorX.next();
                    else nextX = null;
                    if (iteratorY.hasNext()) nextY = iteratorY.next();
                    else nextY = null;
                }
            };
        }
    }
}
