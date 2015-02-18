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

package ru.android_cnc.acnc.Interpreter.State;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.Overrides.OverrideSwitch;

public class FeedRate extends OverrideSwitch {

	private double max_ = 2000; // mm/sec
	private double default_ = 2000; 
	private double current_ = 0;
	
	public FeedRate(){
		//TODO save & restore
	}
	
	public double getDefault() {
		return default_;
	}

	public void setDefault(double defaultFeedRate) {
		if( defaultFeedRate <= this.max_ ){
			this.default_ = defaultFeedRate;
		} else {
			this.default_ = this.max_;
		}
	}

	public double getMax() {
		return max_;
	}

	public void setMax(double maxFeedRate) {
		this.max_ = maxFeedRate;
		if( this.default_ > this.max_ ) 
			this.default_ = this.max_;
	}

	public double get() {
		if(this.OverrideEnabled()&&this.isOverride()) return this.default_;
		else return current_;
	}

	public void set(double newCurrentFeedRate) throws InterpreterException {
		double fr = InterpreterState.modalState.toMM(newCurrentFeedRate);
		if( fr <= this.max_ ){
			this.current_ = fr;
		} else {
			this.current_ = this.max_;
		}
	}

	public double getRapidFeedRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getWorkFeedRate() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
