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

            double angle = command.getStartTangentAngle();

            planX.add(new StepPlanRecord(0, new Step(false, (dx >= 0.0)), null));
            if (Math.abs(dx) > 0) {
                // generate x steps positions
                double dl = Math.abs(step_x/Math.cos(angle));
                double pos = 0;
                while (pos < length) {
                    planX.add(new StepPlanRecord(pos + dl/2, sx, null));
                    pos += dl;
                }
            }
            planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));

            planY.add(new StepPlanRecord(0, null, new Step(false, (dy >= 0.0))));
            if (Math.abs(dy) > 0) {
                // generate y steps positions
                double dl = Math.abs(step_x/Math.sin(angle));
                double pos = 0;
                while (pos < length) {
                    planY.add(new StepPlanRecord(pos + dl/2, null, sy));
                    pos += dl;
                }
            }
            planY.add(new StepPlanRecord(length, null, new Step(false, (dy >= 0.0))));
        };

        if (command instanceof CCommandArcLine) {

            CCommandArcLine arcCommand = (CCommandArcLine) command;
            ArcDirection arcDirection = arcCommand.getArcDirection();
            double radius = arcCommand.radius();
            double startAngle = arcCommand.getStartRadialAngle();
            double endAngle = arcCommand.getEndRadialAngle();
            boolean counterClockWise = (arcDirection == ArcDirection.COUNTERCLOCKWISE);

            // generate x steps positions
            double a = startAngle;
            double x = arcCommand.getStart().getX() - arcCommand.getCenter().getX();
            double shift;
            if(counterClockWise) shift = -step_x;
            else shift = step_x;
            if(Math.sin(a) < 0.0) shift = -shift;
            planX.add(new StepPlanRecord(0, new Step(false, (shift>0.0)), null));
            while(a < endAngle){
                double next_x = x + shift;
                if(Math.abs(x) <= radius){
                    double new_a = Math.acos(next_x/radius);
                    if(a >  Math.PI) new_a = 2.0*Math.PI - new_a;
                    else
                        if(a < -Math.PI) new_a =   new_a - 2.0*Math.PI;
                        else if(a < 0.0) new_a = - new_a;
                    double l = ((new_a - a)/2 - startAngle)*radius;
                    a = new_a;
                    planX.add(new StepPlanRecord(l, new Step(true,  (shift>0.0)), null));
                } else {
                    // insert dir change pulse
                    shift = -shift;
                    if(Math.cos(a) > 0.0){
                        // pass through the 0 or 2*PI or -2*PI angle
                        if(counterClockWise) {
                            if(a >  Math.PI) a =  2.0*Math.PI;
                            else a = 0.0;
                        }  else {
                            if(a < -Math.PI) a = -2.0*Math.PI;
                            else a = 0.0;
                        };
                    } else {
                        // pass through the PI or -PI angle
                        if(a > 0) a = Math.PI;
                        else a = -Math.PI;
                    }
                    double l = radius*(a - startAngle);
                    planX.add(new StepPlanRecord(l, new Step(false, (shift>0.0)), null));
                }
            }
            planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));

            // generate y steps positions
            a = startAngle;
            double y = arcCommand.getStart().getY() - arcCommand.getCenter().getY();
            if(counterClockWise) shift = step_y;
            else shift = -step_y;
            if(Math.cos(a) < 0.0) shift = -shift;
            planY.add(new StepPlanRecord(0, new Step(false, (shift>0.0)), null));
            while(a < endAngle){
                double next_y = y + shift;
                if(Math.abs(y) <= radius){
                    double new_a = Math.asin(next_y / radius);
                    if(a >  Math.PI) new_a = 2.0*Math.PI - new_a;
                    else
                        if(a < -Math.PI) new_a = new_a - 2.0*Math.PI;
                        else if(a < 0.0)new_a = - new_a;
                    double l = ((new_a - a)/2 - startAngle)*radius;
                    a = new_a;
                    planX.add(new StepPlanRecord(l, new Step(true,  (shift>0.0)), null));
                } else {
                    // insert dir change pulse
                    shift = -shift;
                    if(Math.cos(a) > 0.0){
                        // pass through the 0 or 2*PI or -2*PI angle
                        if(counterClockWise) {
                            if(a >  Math.PI) a =  2.0*Math.PI;
                            else a = 0.0;
                        }  else {
                            if(a < -Math.PI) a = -2.0*Math.PI;
                            else a = 0.0;
                        };
                    } else {
                        // pass through the PI or -PI angle
                        if(a > 0) a = Math.PI;
                        else a = -Math.PI;
                    }
                    double l = radius*(a - startAngle);
                    planX.add(new StepPlanRecord(l, new Step(false, (shift>0.0)), null));
                }
            }
            planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));
        };

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
