package ru.android_cnc.acnc.HAL.MotionController;

import java.util.ArrayList;

import ru.android_cnc.acnc.Interpreter.Exceptions.ExecutionException;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlan {

    private ArrayList<StepPlanRecord> plan;

    public StepPlan(MotionControllerCommand command) throws ExecutionException {
        ArrayList<StepPlanRecord> planX = new ArrayList();
        ArrayList<StepPlanRecord> planY = new ArrayList();
        final double step_x = MotionControllerService.getX_mm_in_step();
        final double step_y = MotionControllerService.getY_mm_in_step();
        final double length = command.length();
        if(command instanceof CCommandStraightLine){
            // generate x steps positions
            final double dx = command.getDX();
            final int n_x = (int)Math.abs(dx/step_x);
            if( n_x > 0){
                final double dl = length/(n_x + 1.0);
                for(int i=0; i<n_x; i++){
                    double step_pos = dl/2.0 + i*dl;
                    planX.add(new StepPlanRecord(step_pos, StepPlanRecord.StepTag.X));
                }
            }
            // generate y steps positions
            final double dy = command.getDY();
            final int n_y = (int)Math.abs(dy/step_y);
            if( n_y > 0){
                final double dl = length/(n_y + 1.0);
                for(int i=0; i<n_y; i++){
                    double step_pos = dl/2.0 + i*dl;
                    planY.add(new StepPlanRecord(step_pos, StepPlanRecord.StepTag.Y));
                }
            }
            // merge arrays
            if(n_x>0){
                plan = planX;
                for(int i = (n_y-1); i >= 0; i++){
                    StepPlanRecord recordY = planY.get(i);
                    double y_pos = recordY.getPosition();
                    int pos_in_planX = (int)((n_x+1.0)*y_pos/length - 0.5);
                    plan.add(pos_in_planX, recordY);
                }
            } else plan = planY;
        };
        if(command instanceof CCommandArcLine){

        };
        throw new ExecutionException();
    }

}
