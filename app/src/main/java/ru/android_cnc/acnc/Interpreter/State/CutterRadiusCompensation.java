/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

import ru.android_cnc.acnc.Interpreter.InterpreterException;

public class CutterRadiusCompensation {
	
	private double offset_ = 0.0;
	private mode mode_ = mode.OFF;
	
	public CutterRadiusCompensation(mode m, double r) throws InterpreterException {
		setMode(m);
		setRadius(r);
	}
	
	public void setRadius(double r) throws InterpreterException{
		if(r < 0.0) throw new InterpreterException("Negative cutter radius");
		offset_ = r;
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
	
	public enum mode {
		OFF,
		LEFT,
		RIGHT
	}

}