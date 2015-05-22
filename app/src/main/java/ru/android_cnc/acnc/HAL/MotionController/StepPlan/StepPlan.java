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
    public StepDerivatives derivativesX = new StepDerivatives();
    public StepDerivatives derivativesY = new StepDerivatives();
    private final static double Pi = Math.PI;

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

            // generate x steps positions
            planX.add(new StepPlanRecord(0, new Step(false, (dx >= 0.0)), null));
            if (Math.abs(dx) > 0) {
                double dl = Math.abs(step_x/Math.cos(angle));
                double pos = 0;
                while (pos < length) {
                    planX.add(new StepPlanRecord(pos + dl/2, sx, null));
                    derivativesX.addMeasurement(dl);
                    pos += dl;
                }
            }
            planX.add(new StepPlanRecord(length, new Step(false, (dx >= 0.0)), null));

            // generate y steps positions
            planY.add(new StepPlanRecord(0, null, new Step(false, (dy >= 0.0))));
            if (Math.abs(dy) > 0) {
                double dl = Math.abs(step_y/Math.sin(angle));
                double pos = 0;
                while (pos < length) {
                    planY.add(new StepPlanRecord(pos + dl/2, null, sy));
                    derivativesY.addMeasurement(dl);
                    pos += dl;
                }
            }
            planY.add(new StepPlanRecord(length, null, new Step(false, (dy >= 0.0))));
        }

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
            // define x change for starting motion direction
            if(Math.sin(startAngle)>0) shift = step_x;
            else shift = -step_x;
            if(counterClockWise) shift = -shift;

            planX.add(new StepPlanRecord(0,
                                         new Step(false, (shift>0.0)),
                                         null));
            while(a < endAngle){
                double next_x = x + shift;
                if(Math.abs(next_x) <= radius){

                    double next_a = Math.acos(next_x/radius);
                    // change arccosinus value according previous angle value
                    if(a >  Pi)     next_a = - next_a + 2.0*Pi;
                    else
                        if(a < -Pi) next_a =   next_a - 2.0*Pi;
                        else if(a < 0.0) next_a = - next_a;
                    //
                    double bisection_angle = (next_a + a)/2;
                    planX.add(new StepPlanRecord((bisection_angle - startAngle)*radius,
                                                 new Step(true,  (shift>0.0)),
                                                 null));
                    derivativesX.addMeasurement((next_a - a) * radius);
                    x = next_x;
                    a = next_a;
                } else {
                    // insert dir change pulse
                    shift = -shift;
                    a = Pi * Math.round(a/Pi);
                    planX.add(new StepPlanRecord((a - startAngle)*radius,
                                                  new Step(false, (shift>0.0)),
                                                  null));
                }
            }
            planX.add(new StepPlanRecord(length, new Step(false, (shift>0.0)), null));

            // generate y steps positions
            a = startAngle;
            double y = arcCommand.getStart().getY() - arcCommand.getCenter().getY();
            // define y change for starting motion direction
            if(Math.cos(startAngle)>0) shift = -step_y;
            else shift = step_y;
            if(counterClockWise) shift = -shift;

            planY.add(new StepPlanRecord(0,
                                         null,
                                         new Step(false, (shift>0.0))));
            while(a < endAngle){
                double next_y = y + shift;
                if(Math.abs(next_y) <= radius){

                    double next_a = Math.asin(next_y / radius);
                    // change arcsinus value according previous angle value
                    if(a >  Pi/2)
                        if(a >  3*Pi/2) next_a =   next_a + 2.0*Pi;
                        else            next_a = - next_a + Pi;
                    if(a < -Pi/2)
                        if(a < -3*Pi/2) next_a =   next_a - 2.0*Pi;
                        else            next_a = - next_a - Pi;
                    //
                    double bisection_angle = (next_a + a)/2;
                    planY.add(new StepPlanRecord((bisection_angle - startAngle)*radius,
                                                 null,
                                                 new Step(true,  (shift>0.0))));
                    derivativesY.addMeasurement((next_a - a) * radius);
                    y = next_y;
                    a = next_a;
                } else {
                    // insert dir change pulse
                    shift = -shift;
                    a = Pi * Math.round((a + Pi/2)/Pi) - Pi/2;
                    planY.add(new StepPlanRecord((a - startAngle)*radius,
                                                 null,
                                                 new Step(false, (shift>0.0))));
                }
            }
            planY.add(new StepPlanRecord(length,
                                         null,
                                         new Step(false, (shift>0.0))));
        }

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
            }
        }
    }
}
