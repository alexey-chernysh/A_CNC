package ru.android_cnc.acnc.HAL.MotionController;

import java.util.ArrayList;

import ru.android_cnc.acnc.Interpreter.Exceptions.ExecutionException;

/**
 * Created by Sales on 18.05.2015.
 */

public class StepPlan {

    private ArrayList<StepPlanRecord> plan;

    public StepPlan(MotionControllerCommand command, StepPlanRecord.StepTag t) throws ExecutionException {
        ArrayList<StepPlanRecord> planX = new ArrayList();
        ArrayList<StepPlanRecord> planY = new ArrayList();
        if(command instanceof CCommandStraightLine){
            double length = command.length();
            if(t.isX()){
                double dx = command.getDX();
                if(Math.abs(dx)>0.0){

                }
            }
        };
        if(command instanceof CCommandArcLine){

        };
        throw new ExecutionException();
    }

}
