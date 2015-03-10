/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

import ru.android_cnc.acnc.Draw.DrawableAttributes;
import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

import static ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandStraightLine.normalizeInRadian;
import static ru.android_cnc.acnc.Geometry.CNCPoint.distance;
import static ru.android_cnc.acnc.Geometry.CNCPoint.getCrossArcNArc;
import static ru.android_cnc.acnc.Geometry.CNCPoint.getCrossLineNArc;
import static ru.android_cnc.acnc.Geometry.CNCPoint.getCrossLineNLine;

public class CanonCommandSequence {
	
	private ArrayList<CanonCommand> seq_;
    private DrawableObjectLimits limits;

	public CanonCommandSequence(){
		seq_ = new ArrayList<CanonCommand>();
        limits = new DrawableObjectLimits(Float.MAX_VALUE,
                                         -Float.MAX_VALUE,
                                          Float.MAX_VALUE,
                                         -Float.MAX_VALUE);
	}
	
	public void add(CanonCommand command) throws InterpreterException {
        if(command != null)
            if(command.getType() == CanonCommand.type.MOTION){
                CCommandMotion motion = (CCommandMotion)command;
                if(motion.getMotionType() == CCommandMotion.MotionType.STRAIGHT){
                    if(((CCommandStraightLine) command).isFreeRun())
                        addFreeMotion((CCommandStraightLine)motion);
                    else addCuttingStraightMotion((CCommandStraightLine)motion);
                } else {
                    if(motion.getMotionType() == CCommandMotion.MotionType.ARC){
                        addCuttingArcMotion((CCommandArcLine)motion);
                    }
                    else throw new InterpreterException("Unsupported command");
                }
            } else seq_.add(command);
	}

    public void prepare() throws InterpreterException {
        checkLimits();
        logIt();
    }

    private void checkLimits() throws InterpreterException {
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            CanonCommand command = seq_.get(i);
            if(command.getType() == CanonCommand.type.MOTION){
                CCommandMotion motion = (CCommandMotion)command;
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

	private void addFreeMotion(CCommandStraightLine command) throws InterpreterException {
		CCommandMotion lastMotion = findLastMotion();
		if(lastMotion != null){ 
			// last motion is straight or arc working run, correction needed
            command.setStart(lastMotion.getEnd());
		}
		seq_.add(command);
	}

	@SuppressLint("LongLogTag")
    private void addCuttingStraightMotion(CCommandStraightLine command) throws InterpreterException {
		CNCPoint unOffsetedStart = command.getStart().clone();
//        Log.i("Line before offset", command.toString());
		command.applyCutterRadiusCompensation();
//        Log.i("Line after offset", command.toString());
		CCommandMotion lastMotion = findLastMotion();
		if(lastMotion != null){ // its no first move
//            Log.i("Line before current", lastMotion.toString());
			if(lastMotion.isFreeRun()) {
				// free run line should be connected to start of new motion
                lastMotion.setEnd(command.getStart());
			} else {
				// cutting motion before this
				float alfaCurrent = (float)command.getStartTangentAngle();
                float alfaPrev = (float)lastMotion.getEndTangentAngle();
				final float d_alfa = (float)normalizeInRadian(alfaCurrent - alfaPrev);
//                Log.i("Angles", " Dir current " + alfaCurrent + "; Dir before" + alfaPrev + "; Diff " + d_alfa);
				switch(command.getOffsetMode().getMode()){
				case LEFT:
					if(d_alfa > 0.0){ // motion direction turn left
						// line turn left and left offset
						if(lastMotion.getMotionType() == CCommandMotion.MotionType.STRAIGHT){
						    // Straight line before
                            CCommandStraightLine line = (CCommandStraightLine)lastMotion;
                            CNCPoint connectionPoint = getCrossLineNLine(line, command);
                            if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                            line.setEnd(connectionPoint);
                            command.setStart(connectionPoint);
						} else {
							// arc line before 
                            if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                CNCPoint connectionPoint = getCrossLineNArc(command, arc, CNCPoint.ConnectionType.STARTEND);
                                if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                arc.setEnd(connectionPoint);
                                command.setStart(connectionPoint);
                            }
						};
					} else {
						if((d_alfa < 0.0)&&(command.getOffsetMode().getRadius()>0.0)){
							// line turn right and left offset
							// linking arc with cutter's radius needed
							CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
									  				                   command.getStart(),
									  				                   unOffsetedStart,
									  				                   ArcDirection.CLOCKWISE,
									  				                   command.getVelocityPlan(),
									  				                   command.getOffsetMode());
							seq_.add(link);
						} else {
                            // smooth line connection
//                            Log.i("Smooth line connection", " Point distance is " + distance(lastMotion.getEnd(),command.getStart()));
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
													 command.getVelocityPlan(),
													 command.getOffsetMode());
						seq_.add(newArc);
					} else {
						if(d_alfa < 0.0){
							// line turn right and right offset
							if(lastMotion.getMotionType() == CCommandMotion.MotionType.STRAIGHT){
							    // straight line before
                                CCommandStraightLine line = (CCommandStraightLine)lastMotion;
                                CNCPoint connectionPoint = getCrossLineNLine(line, command);
                                if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                line.setEnd(connectionPoint);
                                command.setStart(connectionPoint);
							} else {
								// arc line before 
                                if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                    CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                    CNCPoint connectionPoint = getCrossLineNArc(command, arc, CNCPoint.ConnectionType.STARTEND);
                                    if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                    arc.setEnd(connectionPoint);
                                    command.setStart(connectionPoint);
                                }
							};
						} else {
                            // smooth line connection
//                            Log.i("Smooth line connection", " Point distance is " + distance(lastMotion.getEnd(),command.getStart()));
                        }
					};
					break;
				case OFF:
				default:
					break;
				}
			}
		}
		seq_.add(command);
	}

	private void addCuttingArcMotion(CCommandArcLine command) throws InterpreterException {
		CNCPoint unOffsetedStart = command.getStart().clone();
		command.applyCutterRadiusCompensation();
		CCommandMotion lastMotion = findLastMotion();
		if(lastMotion != null){ // its no first move
			if(lastMotion.isFreeRun()) {
				// free run line should be connected to start of new motion
				CNCPoint lastEnd = lastMotion.getEnd();
				CNCPoint newStart = command.getStart();
				if(distance(newStart, lastEnd) > 0.0){
					CCommandStraightLine link = new CCommandStraightLine(lastEnd,
											   newStart, 
											   lastMotion.getVelocityPlan(), 
											   MotionMode.FREE, 
											   lastMotion.getOffsetMode());
					seq_.add(link);
				}
			} else {
				// cutting motion before this
                float alfaCurrent = (float)command.getStartTangentAngle();
                            float alfaPrev = (float)lastMotion.getEndTangentAngle();
                            final float d_alfa = (float)normalizeInRadian(alfaCurrent - alfaPrev);
                            switch(command.getOffsetMode().getMode()){
                                case LEFT:
                                    if(d_alfa > 0.0){
                                        // line turn left and left offset
                                        if(lastMotion.getMotionType() == CCommandMotion.MotionType.STRAIGHT){  // Straight line before
                                            CCommandStraightLine line = (CCommandStraightLine)lastMotion;
                                            CNCPoint connectionPoint = getCrossLineNArc(line, command, CNCPoint.ConnectionType.ENDSTART);
                                            if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                            line.setEnd(connectionPoint);
							command.setStart(connectionPoint);
						} else {
							// arc line before 
                            if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                CNCPoint connectionPoint = getCrossArcNArc(arc, command);
                                if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                arc.setEnd(connectionPoint);
                                command.setStart(connectionPoint);
                            }
						};
					} else {
						if(d_alfa < 0.0){
							// line turn right and left offset
							// linking arc with kerf offset radius needed
							CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
									  					command.getStart(),
									  					unOffsetedStart,
									  					ArcDirection.CLOCKWISE,
									  					command.getVelocityPlan(),
									  					command.getOffsetMode());
							seq_.add(link);
						}else {
                            // smooth line connection
//                            Log.i("Smooth line connection", " Point distance is " + distance(lastMotion.getEnd(),command.getStart()));
                        }
					}
					break;
				case RIGHT:
					if(d_alfa > 0.0){
						// line turn left and right offset
						// linking arc with kerf offset radius needed
						CCommandArcLine newArc = new CCommandArcLine(lastMotion.getEnd(),
														command.getStart(),
														unOffsetedStart,
								  						ArcDirection.COUNTERCLOCKWISE,
									  					command.getVelocityPlan(),
									  					command.getOffsetMode());
						seq_.add(newArc);
					} else {
						if(d_alfa < 0.0){
							// line turn right and right offset
							if(lastMotion.getMotionType() == CCommandMotion.MotionType.STRAIGHT){  // stright line before
                                CCommandStraightLine line = (CCommandStraightLine)lastMotion;
								CNCPoint connectionCNCPoint = getCrossLineNArc(line, command, CNCPoint.ConnectionType.ENDSTART);
								line.setEnd(connectionCNCPoint);
								command.setStart(connectionCNCPoint);
							} else { // arc line before 
                                if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                    CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                    CNCPoint connectionCNCPoint = getCrossArcNArc(arc, command);
                                    arc.setEnd(connectionCNCPoint);
                                    command.setStart(connectionCNCPoint);
                                }
							};
						}else {
                            // smooth line connection
//                            Log.i("Smooth line connection", " Point distance is " + distance(lastMotion.getEnd(),command.getStart()));
                        }
					};
					break;
				case OFF:
				default:
					break;
				}
			}
		}
		seq_.add(command);
	}
	
	private CCommandMotion findLastMotion() throws InterpreterException {
		int size = seq_.size();
		for(int i = (size-1); i>=0; i--){
			CanonCommand command = seq_.get(i);
            if(command.getType() == CanonCommand.type.MOTION) return (CCommandMotion)command;
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
