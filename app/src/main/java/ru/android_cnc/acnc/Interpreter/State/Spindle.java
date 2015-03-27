/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

import ru.android_cnc.acnc.Interpreter.State.Overrides.OverrideSwitch;

public class Spindle extends OverrideSwitch {

	private double max_ = 2000; 
	private double default_ = 2000; 
	private double current_ = default_; 
	private State state_ =  State.OFF;
    private Direction direction_ = Direction.CLOCKWISE;
	
	public Spindle(){
		//TODO save & restore
	}
	
	public double getDefault() {
		return default_;
	}
	public void setDefault(double defaultSpindleRate) {
		if( defaultSpindleRate <= this.max_ ){
			this.default_ = defaultSpindleRate;
		} else {
			this.default_ = this.max_;
		}
	}

	public double getMax() {
		return max_;
	}
	public void setMax(double maxSpindleRate) {
		this.max_ = maxSpindleRate;
		if( this.default_ > this.max_ ) 
			this.default_ = this.max_;
	}

	public double getSpeed() {
		if(this.OverrideEnabled()&&this.isOverride()) return this.default_;
		else return current_;
	}
	public void setSpeed(double newCurrentSpindleRate) {
		if( newCurrentSpindleRate <= this.max_ ){
			this.current_ = newCurrentSpindleRate;
		} else {
			this.current_ = this.max_;
		}
	}

	public State getState() {
		return this.state_;
	}
	public void setState(State newState) { this.state_ = newState; }

    public Direction getDirection(){return this.direction_;}
    public void setDirection(Direction d){this.direction_ = d;}

	public enum State {
		OFF,
        ON
	}
    public enum Direction {
        CLOCKWISE,
        COUNTERCLOCKWISE
    }
}
