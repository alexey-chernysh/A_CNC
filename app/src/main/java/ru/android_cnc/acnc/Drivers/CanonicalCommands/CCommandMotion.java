package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.CutterRadiusCompensation;

/**
 * Created by Sales on 05.03.2015.
 */
public abstract class CCommandMotion extends CanonCommand {

    // straight line & arc common fields
    protected CNCPoint start_;
    protected CNCPoint end_;

    private MotionMode mode_;
    private VelocityPlan velocityPlan_;
    private CutterRadiusCompensation offsetMode_;
    private MotionType motionType;
    public DrawableObjectLimits limits = null;

    public CCommandMotion(MotionType mt,
                          CNCPoint s,
                          CNCPoint e,
                          VelocityPlan vp,
                          MotionMode m,
                          CutterRadiusCompensation crc) throws InterpreterException {
        super(CanonCommand.type.MOTION);
        motionType = mt;

        if(s != null) start_ = s;
        else throw new InterpreterException("Null start point in motion command");
        if(e != null) end_ = e;
        else throw new InterpreterException("Null end point in motion command");

        velocityPlan_ = vp;
        mode_ = m;
        offsetMode_ = crc.clone();
    }

    public CNCPoint getStart() { return start_; }
    public void setStart(CNCPoint p) {	this.start_ = p; }
    public CNCPoint getEnd() {	return end_; }
    public void setEnd(CNCPoint p) { this.end_ = p; }

    public double getDX(){ return end_.getX() - start_.getX(); }
    public double getDY(){ return end_.getY() - start_.getY(); }


    public CutterRadiusCompensation getOffsetMode() { return offsetMode_; }
    public void setOffsetMode(CutterRadiusCompensation om) throws InterpreterException { this.offsetMode_ = new CutterRadiusCompensation(om.getMode(), om.getRadius()); }
    public double getOffsetRadius(){
        return offsetMode_.getRadius();
    }

    public VelocityPlan getVelocityPlan() {	return velocityPlan_; }
    private void setVelocityPlan(VelocityPlan vp) {	this.velocityPlan_ = new VelocityPlan(vp.getStartVel(),vp.getEndVel()); }

    public MotionType getMotionType() {
        return motionType;
    }
    public void setMotionType(MotionType motionType) {
        this.motionType = motionType;
    }

    public MotionMode getMode() { return mode_; }
    public boolean isWorkingRun(){ return (this.getMode() == MotionMode.WORK); }
    public boolean isFreeRun(){	return (this.getMode() == MotionMode.FREE);	}

    public abstract void checkLimits() throws InterpreterException;
    public abstract void applyCutterRadiusCompensation();
    public abstract double length();
    public abstract double getStartTangentAngle();
    public abstract double getEndTangentAngle();
    public abstract void setVelocityProfile(double startVel, double endVel);

    @Override
    public void execute() {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    public static double normalizeInRadian(double angle){
        while(angle >   Math.PI) angle -= 2*Math.PI;
        while(angle <  -Math.PI) angle += 2*Math.PI;
        return angle;
    }

    public enum MotionType{
        STRAIGHT,
        ARC
    }
}