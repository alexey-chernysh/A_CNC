/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

public class CutterRadiusCompensation {
	
	private double offset_ = 0.0;
	private mode mode_ = mode.OFF;
	
	public CutterRadiusCompensation(mode m, double r){
		setMode(m);
		setRadius(r);
	}
	
	public void setRadius(double r){
		if(r < 0.0) offset_ = 0.0;
		else offset_ = r;
	}
	
	public void setMode(mode m){
		mode_ = m;
	}
	public mode getMode(){
		return mode_;
	}
	public double getRadius(){
		return Math.abs(offset_);
	}

    @Override
    public CutterRadiusCompensation clone() {
        return new CutterRadiusCompensation(mode_, offset_);
    }

    public enum mode {
		OFF,
		LEFT,
		RIGHT
	}

}