/*
  * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.Draw.DrawableAttributes;
import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

import static android.os.SystemClock.sleep;

public class CCommandStraightLine extends CCommandMotion {


	public CCommandStraightLine(CNCPoint s,
                                CNCPoint e,
                                VelocityPlan vp,
                                MotionMode m,
                                CutterRadiusCompensation crc) throws InterpreterException {
		// init fields
		super(MotionType.STRAIGHT, s, e, vp, m, crc);
	}

    @Override
    public void checkLimits() throws InterpreterException {
        this.limits = new DrawableObjectLimits(this.getStart());
        this.limits = DrawableObjectLimits.combine(this.limits, this.getEnd());
    }

    @Override
	public void applyCutterRadiusCompensation(){
		if(getOffsetMode().getMode() != CutterRadiusCompensation.mode.OFF) {
			double kerf_offset = getOffsetMode().getRadius();
			double alfa = getStartTangentAngle();
			if(getOffsetMode().getMode() != CutterRadiusCompensation.mode.LEFT) alfa += Math.PI/2;
			else alfa -= Math.PI/2;
			double dx = kerf_offset*Math.cos(alfa);
			double dy = kerf_offset*Math.sin(alfa);
			start_ = new CNCPoint(start_.getX()-dx, start_.getY()-dy);
			end_ = new CNCPoint(end_.getX()-dx, end_.getY()-dy);
		}
	}

    @Override
	public double length(){
		double dx = this.getDX();
		double dy = this.getDY();
		return Math.sqrt(dx*dx + dy*dy);
	}

    @Override
	public double getStartTangentAngle() { return normalizeInRadian(Math.atan2(this.getDY(), this.getDX())); }

    @Override
	public double getEndTangentAngle() { return getStartTangentAngle();	}

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
		return new CCommandStraightLine(newStart, newEnd, getVelocityPlan(), getMode(), getOffsetMode());
	}

    @Override
    public void setVelocityProfile(double startVel, double endVel) {
		getVelocityPlan().setStartVel(startVel);
        getVelocityPlan().setEndVel(endVel);
	}

    @Override
    public void execute() {
        double dl = 30.0/10.0; // 30.0 mm/sec ~= 2000 mm/min, refesh 10 times in sec
        double l = length();
        double p;
        while((p=getMotionPhase())<l){
            setMotionPhase(Math.min(p+=dl,l));
            sleep(100);
        }
    }

    @Override
    public void draw(Canvas canvas){
        double p = getMotionPhase();
        double l = length();
        if(p <= 0.0)
            canvas.drawLine((float)this.getStart().getX(),
                            (float)this.getStart().getY(),
                            (float)this.getEnd().getX(),
                            (float)this.getEnd().getY(),
                            DrawableAttributes.getPaintBefore(this.getOffsetMode()));
        else
            if(p >= l)
                canvas.drawLine((float)this.getStart().getX(),
                        (float)this.getStart().getY(),
                        (float)this.getEnd().getX(),
                        (float)this.getEnd().getY(),
                        DrawableAttributes.getPaintAfter(this.getOffsetMode()));
            else {
                double tmp = p/l;
                float tmp_x = (float)(this.getStart().getX() + tmp*this.getDX());
                float tmp_y = (float)(this.getStart().getY() + tmp*this.getDY());
                canvas.drawLine((float)this.getStart().getX(),
                                (float)this.getStart().getY(),
                                tmp_x,
                                tmp_y,
                                DrawableAttributes.getPaintAfter(this.getOffsetMode()));
                canvas.drawLine(tmp_x,
                                tmp_y,
                                (float)this.getEnd().getX(),
                                (float)this.getEnd().getY(),
                                DrawableAttributes.getPaintBefore(this.getOffsetMode()));
            }
    };

    @Override
    public String toString(){
        String result = "Straight motion: ";

        if(isFreeRun()) result += "Free run";
        else result += "Work run";

        result += " from " + start_.toString() + " to " + end_;

        return result;
    }

}
