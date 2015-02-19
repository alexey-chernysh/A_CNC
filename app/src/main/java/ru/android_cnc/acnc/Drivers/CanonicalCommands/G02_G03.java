/*
  * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

public class G02_G03 extends G00_G01 {
	
	// arc specific fields
	protected CNCPoint center_;
	private ArcDirection arcDirection_;
	public static final double arcTol = 0.000001;

	public G02_G03(CNCPoint startCNCPoint,
				   CNCPoint endCNCPoint,
				   CNCPoint centerCNCPoint,
				   ArcDirection arcDirection,
				   VelocityPlan vp,
				   CutterRadiusCompensation offsetMode) throws InterpreterException {
		super(startCNCPoint, endCNCPoint, vp, MotionMode.WORK, offsetMode);

		this.center_ = centerCNCPoint;
		this.arcDirection_ = arcDirection;

	}

	public ArcDirection getArcDirection() {
		return arcDirection_;
	}

	public CNCPoint getCenter() {
		return center_;
	}

	public double getStartRadialAngle() {
		double dx = this.getStart().getX() - this.getCenter().getX();
		double dy = this.getStart().getY() - this.getCenter().getY();
		return Math.atan2(dy, dx);
	}

	@Override
	public double getStartTangentAngle() {
		double alfa = getStartRadialAngle();
		return Radial2Tangent(alfa);
	}
		
	public double getEndRadialAngle() {
		double dx = this.getEnd().getX() - this.getCenter().getX();
		double dy = this.getEnd().getY() - this.getCenter().getY();
		return Math.atan2(dy, dx);
	}
	
	@Override
	public double getEndTangentAngle() {
		double alfa = getEndRadialAngle();
		return Radial2Tangent(alfa);
	}
	
	private double Radial2Tangent(double alfa){
		if(this.getArcDirection() == ArcDirection.CLOCKWISE){
			alfa += Math.PI/2.0;
			if(alfa > Math.PI) alfa -= 2.0 * Math.PI;
		} else {
			alfa -= Math.PI/2.0;
			if(alfa > Math.PI) alfa += 2.0 * Math.PI;
		};
		return alfa;
	}
		
	public double radius(){
		double dx = start_.getX() - center_.getX();
		double dy = start_.getY() - center_.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double angle(){
		double alfa1 = getStartRadialAngle();
		double alfa2 = getEndRadialAngle();
		return (alfa2 - alfa1);
	}
	
	@Override
	public double length(){
		return 2.0 * Math.PI * this.radius() / Math.abs(this.angle());
	}

	public G02_G03 newSubArc(double lengthStart, double lengthEnd) throws InterpreterException {
		CNCPoint newStart = start_;
		CNCPoint newEnd = end_;
		double l = this.length();
		double r = this.radius();
		
		if(lengthStart > 0.0){ // change start point
			double d_a = lengthStart/(Math.PI*r); 
			double a = this.getStartRadialAngle();
			if(this.arcDirection_ == ArcDirection.CLOCKWISE) a += d_a;
			else a -= d_a;
			double x = center_.getX() + r * Math.sin(a);
			double y = center_.getY() + r * Math.cos(a);
			newStart = new CNCPoint(x,y);
		}		
		
		if(lengthEnd < l){  // change end point
			double d_a = (l-lengthEnd)/(Math.PI*r); 
			double a = this.getEndRadialAngle();
			if(this.arcDirection_ == ArcDirection.CLOCKWISE) a -= d_a;
			else a += d_a;
			double x = center_.getX() + r * Math.sin(a);
			double y = center_.getY() + r * Math.cos(a);
			newEnd = new CNCPoint(x,y);
		}
		return new G02_G03(newStart, 
						   newEnd,
						   this.getCenter(),
						   this.getArcDirection(),
						   this.getVelocityPlan(),
						   this.getOffsetMode());
	}

}
