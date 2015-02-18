/*
 * Copyright 2014-2015 Alexey Chernysh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
