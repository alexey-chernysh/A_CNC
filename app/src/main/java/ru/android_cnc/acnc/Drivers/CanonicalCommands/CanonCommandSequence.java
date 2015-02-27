/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;

import java.util.ArrayList;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;

public class CanonCommandSequence {
	
	private ArrayList<CanonCommand> seq_;
	
	public CanonCommandSequence(){
		seq_ = new ArrayList<CanonCommand>();
	}
	
	public void add(CanonCommand command) throws InterpreterException {
        if(command != null)
            if(command.getType() == CanonCommand.type.MOTION){
                if(command instanceof CCommandStraightLine){
                    if(((CCommandStraightLine) command).isFreeRun())	addFreeMotion((CCommandStraightLine) command);
                    else addCuttingStraightMotion((CCommandStraightLine) command);
                } else {
                    if(command instanceof CCommandArcLine) addCuttingArcMotion((CCommandArcLine)command);
                    else throw new InterpreterException("Unsupported command");
                }
            } else seq_.add(command);
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
			// last motion is straight or arc working run, link stright motion may be needed
			CNCPoint lastEnd = lastMotion.getEnd();
			CNCPoint newStart = command.getStart();
			if(CNCPoint.distance(newStart,lastEnd) > 0.0){
				CCommandStraightLine link = new CCommandStraightLine(lastEnd,
										   newStart, 
										   command.getVelocityPlan(), 
										   MotionMode.FREE, 
										   command.getOffsetMode());
				seq_.add(link);
			}
		}
		seq_.add(command);
	}

	private void addCuttingStraightMotion(CCommandStraightLine command) throws InterpreterException {
		CNCPoint unOffsetedStart = command.getStart().clone();
		command.applyCutterRadiusCompensation();
		CCommandStraightLine lastMotion = findLastMotion();
		if(lastMotion != null){ // its no first move 
			if(lastMotion.isFreeRun()) {
				// free run line should be connected to start of new motion
				CNCPoint lastEnd = lastMotion.getEnd();
				CNCPoint newStart = command.getStart();
				if(CNCPoint.distance(newStart,lastEnd) > 0.0){
					CCommandStraightLine link = new CCommandStraightLine(lastEnd,
											   newStart, 
											   lastMotion.getVelocityPlan(), 
											   MotionMode.FREE, 
											   lastMotion.getOffsetMode());
					seq_.add(link);
				}
			} else {
				// cutting motion before this
				double alfaCurrent = command.getStartTangentAngle();
				double alfaPrev = lastMotion.getEndTangentAngle();
				final double d_alfa = alfaCurrent - alfaPrev;
				switch(command.getOffsetMode().getMode()){
				case LEFT:
					if(d_alfa > 0.0){ // motion direction turn left
						// line turn left and left offset
						if(lastMotion instanceof CCommandStraightLine){  // Straight line before
							// calculate length shortening of new line
							double d_l = command.getOffsetMode().getRadius() * Math.sin(d_alfa/2.0);
							// correct previous line
							lastMotion.truncTail(d_l);
							// correct current line
							command.truncHead(d_l);
						} else {
							// arc line before 
							// TODO current algorithm wrong
							CCommandArcLine arc = (CCommandArcLine)lastMotion;
							CNCPoint connectionCNCPoint = getConnectionPoint(command, arc, ConnectionType.STARTEND);
							arc.setEnd(connectionCNCPoint);
							command.setStart(connectionCNCPoint);
						};
					} else {
						if((d_alfa < 0.0)&&(command.getOffsetMode().getRadius()>0.0)){
							// line turn right and left offset
							// linking arc with kerf offset radius needed
							CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
									  				   command.getStart(),
									  				   unOffsetedStart,
									  				   ArcDirection.COUNTERCLOCKWISE,
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
													 ArcDirection.CLOCKWISE,
													 command.getVelocityPlan(),
													 command.getOffsetMode());
						seq_.add(newArc);
					} else {
						if(d_alfa < 0.0){
							// line turn right and right offset
							if(lastMotion instanceof CCommandStraightLine){  // stright line before
								// calc length shortening of new line
								double d_l = command.getOffsetMode().getRadius() * Math.sin(d_alfa/2.0);
								// correct previous line
								lastMotion.truncTail(d_l);
								// correct current line
								command.truncHead(d_l);
							} else {
								// arc line before 
								CCommandArcLine arc = (CCommandArcLine)lastMotion;
								CNCPoint connectionCNCPoint = getConnectionPoint(command, arc, ConnectionType.STARTEND);
								arc.setEnd(connectionCNCPoint);
								command.setStart(connectionCNCPoint);
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
				if(CNCPoint.distance(newStart, lastEnd) > 0.0){
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
							CNCPoint connectionCNCPoint = getConnectionPoint(lm, command, ConnectionType.ENDSTART);
							lastMotion.setEnd(connectionCNCPoint);
							command.setStart(connectionCNCPoint);
						} else {
							// arc line before 
							CCommandArcLine arc = (CCommandArcLine)lastMotion;
							CNCPoint connectionCNCPoint = getConnectionPoint(arc, command);
							arc.setEnd(connectionCNCPoint);
							command.setStart(connectionCNCPoint);
						};
					} else {
						if(d_alfa < 0.0){
							// line turn right and left offset
							// linking arc with kerf offset radius needed
							CCommandArcLine link = new CCommandArcLine(lastMotion.getEnd(),
									  					command.getStart(),
									  					unOffsetedStart,
									  					ArcDirection.COUNTERCLOCKWISE,
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
								  						ArcDirection.CLOCKWISE,
									  					command.getVelocityPlan(),
									  					command.getOffsetMode());
						seq_.add(newArc);
					} else {
						if(d_alfa < 0.0){
							// line turn right and right offset
							if(lastMotion instanceof CCommandStraightLine){  // stright line before
								CNCPoint connectionCNCPoint = getConnectionPoint(lm, command, ConnectionType.ENDSTART);
								lastMotion.setEnd(connectionCNCPoint);
								command.setStart(connectionCNCPoint);
							} else { // arc line before 
								CCommandArcLine arc = (CCommandArcLine)lastMotion;
								CNCPoint connectionCNCPoint = getConnectionPoint(arc, command);
								arc.setEnd(connectionCNCPoint);
								command.setStart(connectionCNCPoint);
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
		for(int i = (size-1); i>0; i--){
			Object command = seq_.get(i);
			if(command instanceof CCommandStraightLine) return (CCommandStraightLine)command;
			if(command instanceof CCommandArcLine) return (CCommandArcLine)command;
		}
		return null;
	}
	
	private enum ConnectionType {
		ENDSTART,
		STARTEND
	}
	
	private CNCPoint getConnectionPoint(CCommandStraightLine Line, CCommandArcLine Arc, ConnectionType type){
		// find connection point of line & circle nearest to end of one & start of another
		double rx = 0.0;
		double ry = 0.0;

		double arccx = Arc.getCenter().getX();
		double arccy = Arc.getCenter().getY();
		double dx = Arc.getStart().getX() - arccx;
		double dy = Arc.getStart().getY() - arccy;
		double arcr = Math.sqrt(dx*dx + dy*dy);

		double lsx = Line.getStart().getX();
		double lsy = Line.getStart().getY();
		double lex = Line.getEnd().getX();
		double ley = Line.getEnd().getY();

		double ldx = lex - lsx;
		double ldy = ley - lsy;
		
		if(Math.abs(ldx)>0){  // line is not vertical
			if(Math.abs(ldy)>0){ // line is not horizontal
				// get canonical formula (y = a*x + b) for line
				double a1 = ldy/ldx;
				double b1 = lsy - a1 * lsx;
				double aD = 1.0 + a1*a1;
				double byc = b1 - arccy;
				double bD = 2.d*(byc*a1 - arccx);
				double cD = arccx*arccx + byc*byc - arcr*arcr;
				double Det = bD*bD - 4.0*aD*cD;
				if(Det<0) Det = 0d;
				double rx1 = (-bD + Math.sqrt(Det))/2/aD;
				double rx2 = (-bD - Math.sqrt(Det))/2/aD;
				switch(type){
				case ENDSTART:
					if( Math.abs(rx1-lex) < Math.abs(rx2-lex) ) rx = rx1;
					else rx = rx2;
					break;
				case STARTEND:
					if( Math.abs(rx1-lsx) < Math.abs(rx2-lsx) ) rx = rx1;
					else rx = rx2;
					break;
				default:
					break;
				}
				ry = a1*rx + b1;
			} else { 
				// line is horizontal 
				// connection is at point with y of line
				ry = ley;
				double t = ry - arccy;
				t = arcr*arcr - t*t;
				if(t>0){
					t = Math.sqrt(t);
					double rx1 = arccx + t;
					double rx2 = arccx - t;
					switch(type){
					case ENDSTART:
						if(Math.abs(rx1-lex) < Math.abs(rx2-lex)) rx = rx1;
						else rx = rx2;
						break;
					case STARTEND:
					default:
						if(Math.abs(rx1-lsx) < Math.abs(rx2-lsx)) rx = rx1;
						else rx = rx2;
						break;
					}
				} else { // tangent line
					 rx = arccx;
				}
			};
		} else {
			// line is vertical
			// connection is at point with x of line
			rx = lex;
			double t = rx - arccx;
			t = arcr*arcr - t*t;
			if(t>0){
				t = Math.sqrt(t);
				double ry1 = arccy + t;
				double ry2 = arccy - t;
				switch(type){
				case ENDSTART:
					if(Math.abs(ry1-ley) < Math.abs(ry2-ley)) ry = ry1;
					else ry = ry2;
					break;
				case STARTEND:
				default:
					if(Math.abs(ry1-lsy) < Math.abs(ry2-lsy)) ry = ry1;
					else ry = ry2;
					break;
				}
			} else { // tangent line
				 ry = arccy;
			}
		}
		return new CNCPoint(rx, ry);
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

    public void draw(Context context, Canvas canvas){
        int seq_length = seq_.size();
        for(int i=0;i<seq_length;i++){
            seq_.get(i).draw(context, canvas);
        }
    }
}
