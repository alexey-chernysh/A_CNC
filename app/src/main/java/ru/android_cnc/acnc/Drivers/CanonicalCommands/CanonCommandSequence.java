/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

import ru.android_cnc.acnc.Draw.DrawableAttributes;
import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.HAL.MotionController.ArcDirection;
import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.HAL.MotionController.MotionControllerCommand;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

import static ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine.normalizeInRadian;
import static ru.android_cnc.acnc.Geometry.CNCPoint.getCrossingPoint;

public class CanonCommandSequence {

    private final static String LOG_TAG = " command sequence ->";

    private ArrayList<CanonCommand> seq_;
    private DrawableObjectLimits limits;

	public CanonCommandSequence(){
		seq_ = new ArrayList<CanonCommand>();
        limits = new DrawableObjectLimits();
	}
	
	public void add(CanonCommand command) throws EvolutionException {
        if(command != null)
            if(command.getType() == CanonCommand.type.MOTION){
                MotionControllerCommand motion = (MotionControllerCommand)command;
                if(motion.isFreeRun()) addFreeMotion(motion);
                else addCuttingMotion(motion);
            } else seq_.add(command);
//        Log.i(LOG_TAG, " add " + command.toString());
	}

    public void prepare() throws EvolutionException {
        checkLimits();
        logIt();
    }

    private void checkLimits() throws EvolutionException {
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            CanonCommand command = seq_.get(i);
            if(command.getType() == CanonCommand.type.MOTION){
                MotionControllerCommand motion = (MotionControllerCommand)command;
                motion.checkLimits();
                limits = DrawableObjectLimits.combine(limits, motion.limits);
            }
        }
    }

    private void logIt(){
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            CanonCommand command = seq_.get(i);
            Log.i("Command ", " n = " + i + " " + command.toString());
        }
    }

    public int size(){
		return seq_.size();
	}
	
	public CanonCommand get(int i){
		return seq_.get(i);
	}

    private void addCuttingMotion(MotionControllerCommand command) throws EvolutionException {
        CNCPoint unOffsetedStart = command.getStart().clone();
        command.applyCutterRadiusCompensation();
        MotionControllerCommand lastMotion = findLastMotion();
        if(lastMotion != null){ // its no first move
            if(lastMotion.isFreeRun()) {
                // free run line should be connected to start of new motion
                lastMotion.setEnd(command.getStart());
            } else {
                // cutting motion before this
                float alfaCurrent = (float)command.getStartTangentAngle();
                float alfaPrev = (float)lastMotion.getEndTangentAngle();
                final float d_alfa = (float)normalizeInRadian(alfaCurrent - alfaPrev);
                switch(command.getOffsetMode().getMode()){
                    case LEFT:
                        if(d_alfa > 0.0){ // motion direction turn left
                            // line turn left and left offset
                            CNCPoint connectionPoint = getCrossingPoint(lastMotion, command);
                            if(connectionPoint == null) throw new EvolutionException("Wrong G-code");
                            lastMotion.setEnd(connectionPoint);
                            command.setStart(connectionPoint);
                        } else {
                            if((d_alfa < 0.0)&&(command.getOffsetMode().getRadius()>0.0)){
                                // line turn right and left offset
                                // linking arc with cutter's radius needed
                                CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
                                                                            command.getStart(),
                                                                            unOffsetedStart,
                                                                            ArcDirection.CLOCKWISE,
                                                                            command.getFeedRate(),
                                                                            command.getOffsetMode());
                                seq_.add(link);
                            }
                        }
                        break;
                    case RIGHT:
                        if(d_alfa > 0.0){
                            // line turn left and right offset
                            // linking arc with cutter's radius needed
                            CCommandArcLine newArc = new CCommandArcLine(lastMotion.getEnd(),
                                                                        command.getStart(),
                                                                        unOffsetedStart,
                                                                        ArcDirection.COUNTERCLOCKWISE,
                                                                        command.getFeedRate(),
                                                                        command.getOffsetMode());
                            seq_.add(newArc);
                        } else {
                            if(d_alfa < 0.0){
                                // line turn right and right offset
                                CNCPoint connectionPoint = getCrossingPoint(lastMotion, command);
                                if(connectionPoint == null) throw new EvolutionException("Wrong G-code");
                                lastMotion.setEnd(connectionPoint);
                                command.setStart(connectionPoint);
                            }
                        }
                        break;
                    case OFF:
                    default:
                        break;
                }
            }
        }
        seq_.add(command);
    }

    private void addFreeMotion(MotionControllerCommand command) throws EvolutionException {
		MotionControllerCommand lastMotion = findLastMotion();
		if(lastMotion != null){ 
			// last motion is straight or arc working run, correction needed
            command.setStart(lastMotion.getEnd());
		}
		seq_.add(command);
	}

	private MotionControllerCommand findLastMotion() throws EvolutionException {
		int size = seq_.size();
		for(int i = (size-1); i>=0; i--){
			CanonCommand command = seq_.get(i);
            if(command.getType() == CanonCommand.type.MOTION) return (MotionControllerCommand)command;
		}
		return null;
	}

    public DrawableObjectLimits getLimits() {
        return limits;
    }

    public void draw(Canvas canvas){
        DrawableAttributes.setWidth((float)InterpreterState.offsetMode.getRadius());
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            seq_.get(i).draw(canvas);
        }
    }

    public void remove(int i) {
        seq_.remove(i);
    }

    public void add(int i, CanonCommand command) {
        seq_.add(i,command);
    }
}
