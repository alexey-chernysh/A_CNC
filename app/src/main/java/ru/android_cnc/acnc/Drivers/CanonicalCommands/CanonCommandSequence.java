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
import static ru.android_cnc.acnc.Geometry.CNCPoint.getCrossingPoint;

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
                if(command instanceof CCommandStraightLine){
                    if(((CCommandStraightLine) command).isFreeRun())
                        addFreeMotion((CCommandStraightLine) command);
                    else addCuttingStraightMotion((CCommandStraightLine) command);
                } else {
                    if(command instanceof CCommandArcLine){
                        addCuttingArcMotion((CCommandArcLine) command);
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
            limits = DrawableObjectLimits.combine(limits, command.limits);
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
		CCommandStraightLine lastMotion = findLastMotion();
		if(lastMotion != null){ 
			// last motion is straight or arc working run, correction needed
            command.setStart(lastMotion.getEnd());
		}
		seq_.add(command);
	}

	@SuppressLint("LongLogTag")
    private void addCuttingStraightMotion(CCommandStraightLine command) throws InterpreterException {
		CNCPoint unOffsetedStart = command.getStart().clone();
        Log.i("Line before offset", command.toString());
		command.applyCutterRadiusCompensation();
        Log.i("Line after offset", command.toString());
		CCommandStraightLine lastMotion = findLastMotion();
		if(lastMotion != null){ // its no first move
            Log.i("Line before current", lastMotion.toString());
			if(lastMotion.isFreeRun()) {
				// free run line should be connected to start of new motion
                lastMotion.setEnd(command.getStart());
			} else {
				// cutting motion before this
				double alfaCurrent = command.getStartTangentAngle();
				double alfaPrev = lastMotion.getEndTangentAngle();
				final double d_alfa = normalizeInRadian(alfaCurrent - alfaPrev);
                Log.i("Angles", " Dir current " + alfaCurrent + "; Dir before" + alfaPrev + "; Diff " + d_alfa);
				switch(command.getOffsetMode().getMode()){
				case LEFT:
					if(d_alfa > 0.0){ // motion direction turn left
						// line turn left and left offset
						if(lastMotion instanceof CCommandStraightLine){
						    // Straight line before
                            CNCPoint connectionPoint = getCrossingPoint(lastMotion, command);
                            if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                            lastMotion.setEnd(connectionPoint);
                            command.setStart(connectionPoint);
						} else {
							// arc line before 
							// TODO current algorithm wrong
                            if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                CNCPoint connectionCNCPoint = getCrossingPoint(command, arc, CNCPoint.ConnectionType.STARTEND);
                                arc.setEnd(connectionCNCPoint);
                                command.setStart(connectionCNCPoint);
                            }
						};
					} else {
						if((d_alfa < 0.0)&&(command.getOffsetMode().getRadius()>0.0)){
							// line turn right and left offset
							// linking arc with kerf offset radius needed
							CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
									  				                   command.getStart(),
									  				                   unOffsetedStart,
									  				                   ArcDirection.CLOCKWISE,
									  				                   command.getVelocityPlan(),
									  				                   command.getOffsetMode());
							seq_.add(link);
						};
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
							if(lastMotion instanceof CCommandStraightLine){
							    // stright line before
                                CNCPoint connectionPoint = getCrossingPoint(lastMotion, command);
                                if(connectionPoint == null) throw new InterpreterException("Wrong G-code");
                                lastMotion.setEnd(connectionPoint);
                                command.setStart(connectionPoint);
							} else {
								// arc line before 
                                if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                    CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                    CNCPoint connectionCNCPoint = getCrossingPoint(command, arc, CNCPoint.ConnectionType.STARTEND);
                                    arc.setEnd(connectionCNCPoint);
                                    command.setStart(connectionCNCPoint);
                                }
							};
						};
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
		CCommandStraightLine lastMotion = findLastMotion();
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
				CCommandStraightLine lm = (CCommandStraightLine)lastMotion;
				double alfaCurrent = command.getStartTangentAngle();
				double alfaPrev = lastMotion.getEndTangentAngle();
				final double d_alfa = alfaCurrent - alfaPrev;
				switch(command.getOffsetMode().getMode()){
				case LEFT:
					if(d_alfa > 0.0){
						// line turn left and left offset
						if(lastMotion instanceof CCommandStraightLine){  // Straight line before
							CNCPoint connectionCNCPoint = getCrossingPoint(lm, command, CNCPoint.ConnectionType.ENDSTART);
							lastMotion.setEnd(connectionCNCPoint);
							command.setStart(connectionCNCPoint);
						} else {
							// arc line before 
                            if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                CNCPoint connectionCNCPoint = getConnectionPoint(arc, command);
                                arc.setEnd(connectionCNCPoint);
                                command.setStart(connectionCNCPoint);
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
						};
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
							if(lastMotion instanceof CCommandStraightLine){  // stright line before
								CNCPoint connectionCNCPoint = getCrossingPoint(lm, command, CNCPoint.ConnectionType.ENDSTART);
								lastMotion.setEnd(connectionCNCPoint);
								command.setStart(connectionCNCPoint);
							} else { // arc line before 
                                if(distance(lastMotion.getEnd(),command.getStart())>0.0){
                                    CCommandArcLine arc = (CCommandArcLine)lastMotion;
                                    CNCPoint connectionCNCPoint = getConnectionPoint(arc, command);
                                    arc.setEnd(connectionCNCPoint);
                                    command.setStart(connectionCNCPoint);
                                }
							};
						};
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
	
	private CCommandStraightLine findLastMotion() {
		int size = seq_.size();
		for(int i = (size-1); i>=0; i--){
			Object command = seq_.get(i);
			if(command instanceof CCommandStraightLine) return (CCommandStraightLine)command;
			if(command instanceof CCommandArcLine) return (CCommandArcLine)command;
		}
		return null;
	}

    public DrawableObjectLimits getLimits() {
        return limits;
    }

	private CNCPoint getConnectionPoint(CCommandArcLine A1, CCommandArcLine A2){
		CNCPoint result;
		
		double dxsa1 = A1.getStart().getX() - A1.getCenter().getX();
		double dysa1 = A1.getStart().getY() - A1.getCenter().getY();
		double dxea1 = A1.getEnd().getX() - A1.getCenter().getX();
		double dyea1 = A1.getEnd().getY() - A1.getCenter().getY();
		double r2a1 = dxsa1*dxsa1 + dysa1*dysa1;
		double ra1 = Math.sqrt(r2a1);

		double dxsa2 = A2.getStart().getX() - A2.getCenter().getX();
		double dysa2 = A2.getStart().getY() - A2.getCenter().getY();
		double r2a2 = dxsa2*dxsa2 + dysa2*dysa2;
		double ra2 = Math.sqrt(r2a2);
		
		double dxc = A2.getCenter().getX() - A1.getCenter().getX();
		double dyc = A2.getCenter().getY() - A1.getCenter().getY();
		double ac = Math.atan2(dyc, dxc);
		double r2c = dxc*dxc + dyc*dyc;
		double rc = Math.sqrt(r2c);
		
		double overlap = rc - ra1 - ra2;
		double meps = 0.001; //Drawing.DwgConst.masheps;
		if(Math.abs(overlap) < meps){ // centers offseted, one connection point
			double xcp = A2.getStart().getX();
			double ycp = A2.getStart().getY();
			result = new CNCPoint(xcp, ycp);
		} else { 
			if(rc < meps){ // centers are equal
				double ae1 = Math.atan2(dyea1, dxea1);
				double as2 = Math.atan2(dysa2, dxsa2);
				double acp = (ae1 + as2)/2d;
				double xcp = A1.getCenter().getX() + ra1*Math.cos(acp);
				double ycp = A1.getCenter().getY() + ra1*Math.sin(acp);
				result = new CNCPoint(xcp, ycp);
			} else {	// different centers, two connection point
				double d1 = (r2a1 - r2a2 + r2c)/(2d*rc);
				double t = r2a1 - d1*d1;
				if(t<0) t = 0;
				double h = Math.sqrt(t);
				double dah = Math.atan2(h, d1);
				// first point
				double xcp1 = A1.getCenter().getX() + ra1*Math.cos(ac-dah);
				double ycp1 = A1.getCenter().getY() + ra1*Math.sin(ac-dah);
				// second point
				double xcp2 = A1.getCenter().getX() + ra1*Math.cos(ac+dah);
				double ycp2 = A1.getCenter().getY() + ra1*Math.sin(ac+dah);
				double dx1 = A1.getEnd().getX() - xcp1;
				double dy1 = A1.getEnd().getY() - ycp1;
				double dx2 = A1.getEnd().getX() - xcp2;
				double dy2 = A1.getEnd().getY() - ycp2;
				if((dx1*dx1 + dy1*dy1)<(dx2*dx2 + dy2*dy2)){ // choose nearest point
					result = new CNCPoint(xcp1, ycp1);
				}else{
					result = new CNCPoint(xcp2, ycp2);
				}
			}
		}
		return result;
	}

    public void draw(Canvas canvas){
        DrawableAttributes.setWidth((float)InterpreterState.offsetMode.getRadius());
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            seq_.get(i).draw(canvas);
        }
    }
}
