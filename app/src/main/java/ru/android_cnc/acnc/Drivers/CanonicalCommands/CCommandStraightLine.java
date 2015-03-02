/*
  * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;

import ru.android_cnc.acnc.GraphView.CNCViewContext;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

public class CCommandStraightLine extends CanonCommand {

	// straight line & arc common fields
	protected CNCPoint start_;
	protected CNCPoint end_;

	private MotionMode mode_;
	private VelocityPlan velocityPlan_;
	private CutterRadiusCompensation offsetMode_;

	public CCommandStraightLine(CNCPoint s,
                                CNCPoint e,
                                VelocityPlan vp,
                                MotionMode m,
                                CutterRadiusCompensation crc) throws InterpreterException {
		// all motions are absolute to current home point
		// init fields
		super(CanonCommand.type.MOTION);
		if(s != null) start_ = s;
		else throw new InterpreterException("Null start point in motion command");
		if(e != null) end_ = e;
		else throw new InterpreterException("Null end point in motion command");
		setVelocityPlan(vp);
		mode_ = m;
		setOffsetMode(crc);
	}

	public void applyCutterRadiusCompensation(){
		if(offsetMode_.getMode() != CutterRadiusCompensation.mode.OFF) {
			double kerf_offset = offsetMode_.getRadius();
			double alfa = getStartTangentAngle();
			if(offsetMode_.getMode() != CutterRadiusCompensation.mode.LEFT) alfa += Math.PI/2;
			else alfa -= Math.PI/2;
			double dx = kerf_offset*Math.sin(alfa);
			double dy = kerf_offset*Math.cos(alfa);
			start_ = new CNCPoint(start_.getX()+dx, start_.getY()+dy);
			end_ = new CNCPoint(end_.getX()+dx, end_.getY()+dy);
		}
	}

	public CNCPoint getStart() { return start_; }

	public void setStart(CNCPoint p) {	this.start_ = p; }

	public CNCPoint getEnd() {	return end_; }

	public void setEnd(CNCPoint p) { this.end_ = p; }

	public MotionMode getMode() { return mode_; }

	public double getDX(){ return end_.getX() - start_.getX(); }

	public double getDY(){ return end_.getY() - start_.getY(); }

	public double length(){
		double dx = this.getDX();
		double dy = this.getDY();
		return Math.sqrt(dx*dx + dy*dy);
	}

	public double getStartTangentAngle() { return Math.atan2(this.getDY(), this.getDX()); }

	public double getEndTangentAngle() { return getStartTangentAngle();	}

	public boolean isWorkingRun(){ return (this.getMode() == MotionMode.WORK); }

	public boolean isFreeRun(){	return (this.getMode() == MotionMode.FREE);	}

	public CutterRadiusCompensation getOffsetMode() { return offsetMode_; }

	public VelocityPlan getVelocityPlan() {	return velocityPlan_; }

	private void setVelocityPlan(VelocityPlan vp) {	this.velocityPlan_ = new VelocityPlan(vp.getStartVel(),vp.getEndVel()); }

	public void setOffsetMode(CutterRadiusCompensation om) throws InterpreterException { this.offsetMode_ = new CutterRadiusCompensation(om.getMode(), om.getRadius()); }

	public void truncHead(double dl/* length change */) throws InterpreterException{
		double l = length();
		if(l < dl ) throw new InterpreterException("Line too short for current compensation");
		else {
			double alfa = getEndTangentAngle();
			double dx = dl * Math.sin(alfa);
			double dy = dl * Math.cos(alfa);
			CNCPoint newStart = new CNCPoint(this.getStart().getX()+dx,
									   this.getStart().getY()+dy);
			this.setStart(newStart);
		}
	}

	public void truncTail(double dl/* length change */) throws InterpreterException{
		double l = length();
		if(l < dl ) throw new InterpreterException("Line too short for current compensation");
		else {
			double alfa = getEndTangentAngle();
			double dx = dl * Math.sin(alfa);
			double dy = dl * Math.cos(alfa);
			CNCPoint newEnd = new CNCPoint(this.getEnd().getX()-dx,
									 this.getEnd().getY()-dy);
			this.setEnd(newEnd);
		}
	}

	public CCommandStraightLine newSubLine(double lengthStart, double lengthEnd) throws InterpreterException {

		CNCPoint newStart = start_;
		CNCPoint newEnd = end_;
		double l = this.length();

		if(lengthStart > 0.0){ // change start point
			double x = start_.getX();
			double y = start_.getY();
			double a = getStartTangentAngle();
			x += lengthStart*Math.sin(a);
			y += lengthStart*Math.cos(a);
			newStart = new CNCPoint(x,y);
		}

		if(lengthEnd < l){  // change end point
			double x = newEnd.getX();
			double y = newEnd.getY();
			double a = getEndTangentAngle();
			x += (l-lengthEnd)*Math.sin(a);
			y += (l-lengthEnd)*Math.cos(a);
			newEnd = new CNCPoint(x,y);
		}
		return new CCommandStraightLine(newStart, newEnd, this.velocityPlan_, this.mode_, this.offsetMode_);
	}

	public void setVelocityProfile(double startVel, double endVel) {
		this.velocityPlan_.setStartVel(startVel);
		this.velocityPlan_.setEndVel(endVel);
	}

    @Override
    public void execute() {

    }

    @Override
    public void draw(CNCViewContext context, Canvas canvas){
        canvas.drawLine((float)this.getStart().getX(),
                        (float)this.getStart().getY(),
                        (float)this.getEnd().getX(),
                        (float)this.getEnd().getY(),
                        DrawableAttributes.getPaintBefore(this.getOffsetMode()));
    };

}
