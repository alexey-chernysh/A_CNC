/*
  * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import ru.android_cnc.acnc.Draw.DrawableAttributes;
import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

public class CCommandArcLine extends CCommandStraightLine {
	
	// arc specific fields
	protected CNCPoint center_;
	private ArcDirection arcDirection_;
	public static final double arcTol = 0.000001;

	public CCommandArcLine(CNCPoint startCNCPoint,
                           CNCPoint endCNCPoint,
                           CNCPoint centerCNCPoint,
                           ArcDirection arcDirection,
                           VelocityPlan vp,
                           CutterRadiusCompensation offsetMode) throws InterpreterException {
		super(startCNCPoint, endCNCPoint, vp, MotionMode.WORK, offsetMode);

		this.center_ = centerCNCPoint;
		this.arcDirection_ = arcDirection;

        checkLimits();
	}

    private void checkLimits() throws InterpreterException {
        // start & end points checked in Straight Line constructor
        // so we need check points on arc only
        double alfaStart = this.getStartRadialAngle();
        double alfaEnd = this.getEndRadialAngle();
        double delta = Math.PI/2.0;
        for(int i=0; i<4; i++){
            double beta = delta*i;
            if((beta>alfaStart)&&(beta<alfaEnd)){
                double R = this.radius();
                limits = DrawableObjectLimits.combine(this.limits,
                                                      new CNCPoint(this.getCenter().getX() + R*Math.cos(beta),
                                                                   this.getCenter().getY() + R*Math.sin(beta)));
            }
        }
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

	public CCommandArcLine newSubArc(double lengthStart, double lengthEnd) throws InterpreterException {
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
		return new CCommandArcLine(newStart,
						   newEnd,
						   this.getCenter(),
						   this.getArcDirection(),
						   this.getVelocityPlan(),
						   this.getOffsetMode());
	}

    @Override
    public void execute() {

    }

    @Override
    public void draw(Canvas canvas) {
        double cx = center_.getX();
        double cy = center_.getY();
        double sx = start_.getX();
        double sy = start_.getY();
        double ex = end_.getX();
        double ey = end_.getY();
        double dxs = sx - cx;
        double dys = sy - cy;
        double R1 = Math.sqrt(dxs*dxs + dys*dys);
        RectF rect = new RectF((float)(cx-R1), (float)(cy-R1), (float)(cx+R1), (float)(cy+R1));
        float A = (float)Math.toDegrees(Math.atan2(dys, dxs));
        float B = (float)Math.toDegrees(Math.atan2(ey - cy, ex - cx));
        if(getArcDirection() != ArcDirection.COUNTERCLOCKWISE) { // exchange points
            float T = A; A = B; B = T;
        }
        while(B<=A) B += 360.f;
        B -= A;
        Paint currentPaint = DrawableAttributes.getPaintBefore(this.getOffsetMode());
        boolean atr = R1<=this.getOffsetRadius();
        if(Math.abs(B-A)<360.f)
            canvas.drawArc(rect, A, B, atr, currentPaint);
        else {
            float C = (B + A)/2.0f;
            canvas.drawArc(rect, A, C, atr, currentPaint);
            canvas.drawArc(rect, C, B, atr, currentPaint);
        }
    }
}
