/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.MotionController;

import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;

public class MotionController implements Runnable {
	
/*	
	private double x_scale = 0.00768; // mm/pulse
	private double y_scale = 0.00768; // mm/pulse

	private double tik_length = 3000; // ns
	private double dir_rise_delay = 3000; // ns
	private double dir_down_delay = 3000; // ns
	private double pulse_length = 3000; // ns
	private double pulse_space = 3000; // ns
*/	
	private MCstate currentState = MCstate.EMPTY;

	public enum MCstate {
		EMPTY,
		STARTED,
		PAUSED,
		STOPED;
	}
	
	@Override
	public void run() {
		boolean doRun = true;
		while (doRun){
		}
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public MCstate getCurrentState() {
		return currentState;
	}

	public void setCurrentState(MCstate newState) {
		this.currentState = newState;
	}

	public CNCPoint getCurrentPosition() {
		// TODO Auto-generated method stub
		return null;
	}

}
