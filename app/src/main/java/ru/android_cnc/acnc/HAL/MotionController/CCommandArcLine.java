/*
  * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController;

import android.graphics.Canvas;
import android.graphics.RectF;

import ru.android_cnc.acnc.Draw.DrawableAttributes;
import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CCommandArcLine extends MotionControllerCommand {
	
	// arc specific fields
	protected CNCPoint center_;
	private ArcDirection arcDirection_;

    public CCommandArcLine(CNCPoint startCNCPoint,
                           CNCPoint endCNCPoint,
                           CNCPoint centerCNCPoint,
                           ArcDirection arcDirection,
                           double fr,
                           CutterRadiusCompensation offsetMode) throws EvolutionException {
		super(MotionType.ARC, startCNCPoint, endCNCPoint, fr, MotionMode.WORK, offsetMode);

		this.center_ = centerCNCPoint;
		this.arcDirection_ = arcDirection;
	}

    @Override
    public void applyCutterRadiusCompensation() {
        double R = this.radius();
        double dR = this.getOffsetRadius();
        if(this.getOffsetMode().getMode() == CutterRadiusCompensation.mode.LEFT){
            if(this.arcDirection_ == ArcDirection.CLOCKWISE) R += dR;
            else R -= dR;
        } else {
            if(this.arcDirection_ == ArcDirection.CLOCKWISE) R -= dR;
            else R += dR;
        }
        double startAngle = this.getStartRadialAngle();
        double endAngle = this.getEndRadialAngle();
        double cx = getCenter().getX();
        double cy = getCenter().getY();
        this.setStart(new CNCPoint(cx+R*cos(startAngle),cy+R*sin(startAngle)));
        this.setEnd(new CNCPoint(cx+R*cos(endAngle),cy+R*sin(endAngle)));
    }

    @Override
    public void checkLimits() throws EvolutionException {
        // start & end points checked in Straight Line constructor
        // so we need check points on arc only
        this.limits = new DrawableObjectLimits(this.getStart());
        this.limits = DrawableObjectLimits.combine(this.limits, this.getEnd());
        double alfaStart = this.getStartRadialAngle();
        double alfaEnd = this.getEndRadialAngle();
        double delta = Math.PI/2.0;
        for(int i=0; i<4; i++){
            double beta = delta*i;
            if((beta>alfaStart)&&(beta<alfaEnd)){
                double R = this.radius();
                limits = DrawableObjectLimits.combine(this.limits,
                                                      new CNCPoint(this.getCenter().getX() + R* cos(beta),
                                                                   this.getCenter().getY() + R* sin(beta)));
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
		return normalizeInRadian(Math.atan2(dy, dx));
	}

	@Override
	public double getStartTangentAngle() {
		double alfa = getStartRadialAngle();
		return normalizeInRadian(Radial2Tangent(alfa));
	}
		
	public double getEndRadialAngle() {
		double dx = this.getEnd().getX() - this.getCenter().getX();
		double dy = this.getEnd().getY() - this.getCenter().getY();
		return normalizeInRadian(Math.atan2(dy, dx));
	}
	
	@Override
	public double getEndTangentAngle() {
		double alfa = getEndRadialAngle();
		return normalizeInRadian(Radial2Tangent(alfa));
	}

    private double Radial2Tangent(double alfa){
		if(this.getArcDirection() == ArcDirection.CLOCKWISE) return alfa - Math.PI/2.0;
		else return alfa + Math.PI/2.0;
	}
		
	public double radius(){
		double dx = start_.getX() - center_.getX();
		double dy = start_.getY() - center_.getY();
		return Math.sqrt(dx*dx + dy*dy);
	}
	
	public double angle(){
		double alfa1 = getStartRadialAngle();
		double alfa2 = getEndRadialAngle();
        double result = normalizeInRadian(alfa2 - alfa1);
		if(result == 0.0)
            if(this.getArcDirection() == ArcDirection.CLOCKWISE)return -2.0 * Math.PI;
            else return 2.0 * Math.PI;
        else return result;
	}
	
	@Override
	public double length(){
		return 2.0 * Math.PI * this.radius() / Math.abs(this.angle());
	}

	public CCommandArcLine newSubArc(double lengthStart, double lengthEnd) throws EvolutionException {
		CNCPoint newStart = start_;
		CNCPoint newEnd = end_;
		double l = this.length();
		double r = this.radius();
		
		if(lengthStart > 0.0){ // change start point
			double d_a = lengthStart/(Math.PI*r); 
			double a = this.getStartRadialAngle();
			if(this.arcDirection_ == ArcDirection.CLOCKWISE) a += d_a;
			else a -= d_a;
			double x = center_.getX() + r * cos(a);
			double y = center_.getY() + r * sin(a);
			newStart = new CNCPoint(x,y);
		}		
		
		if(lengthEnd < l){  // change end point
			double d_a = (l-lengthEnd)/(Math.PI*r); 
			double a = this.getEndRadialAngle();
			if(this.arcDirection_ == ArcDirection.CLOCKWISE) a -= d_a;
			else a += d_a;
			double x = center_.getX() + r * cos(a);
			double y = center_.getY() + r * sin(a);
			newEnd = new CNCPoint(x,y);
		}
		return new CCommandArcLine(newStart,
						   newEnd,
						   this.getCenter(),
						   this.getArcDirection(),
						   this.getFeedRate(),
						   this.getOffsetMode());
	}
/*
    @Override
    public void execute() {
    }
*/
    @Override
    public void draw(Canvas canvas) {
        float p = (float)(getMotionPhase()/length());
        double R = this.radius();
        RectF rect = new RectF((float)(this.getCenter().getX()-R),
                               (float)(this.getCenter().getY()-R),
                               (float)(this.getCenter().getX()+R),
                               (float)(this.getCenter().getY()+R));
        float ang = (float)Math.toDegrees(this.angle());
        float ang1 = p*ang;
        float ang2 = ang - ang1;
        float A = (float)Math.toDegrees(this.getStartRadialAngle());
//        Log.i("Drawing arc: ", "from angle - " + A + "; arc angle - " + ang + "; phase- " + ang1);
        if(p <= 0.0)
            canvas.drawArc(rect, A, ang, false, DrawableAttributes.getPaintBefore(this.getOffsetMode()));
        else
        if(p >= 1.0)
            canvas.drawArc(rect, A, ang, false, DrawableAttributes.getPaintAfter(this.getOffsetMode()));
        else {
            canvas.drawArc(rect, A, ang1, false, DrawableAttributes.getPaintAfter(this.getOffsetMode()));
            canvas.drawArc(rect, A+ang1, ang2, false, DrawableAttributes.getPaintBefore(this.getOffsetMode()));
        }
    }

    @Override
    public String toString(){
        return "Arc motion from " + start_.toString() + " to " + end_ + ", center " + center_;
    }

}
