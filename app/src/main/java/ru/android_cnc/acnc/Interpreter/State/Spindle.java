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

import Interpreter.State.Overrides.OverrideSwitch;

public class Spindle extends OverrideSwitch {

	private double max_ = 2000; 
	private double default_ = 2000; 
	private double current_ = default_; 
	private SpindleRotation state =  SpindleRotation.OFF;
	
	public Spindle(){
		//TODO save & restore
	}
	
	public double getDefaulte() {
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

	public double get() {
		if(this.OverrideEnabled()&&this.isOverride()) return this.default_;
		else return current_;
	}

	public void set(double newCurrentSpindleRate) {
		if( newCurrentSpindleRate <= this.max_ ){
			this.current_ = newCurrentSpindleRate;
		} else {
			this.current_ = this.max_;
		}
	}

	public  SpindleRotation getState() {
		return this.state;
	}

	public void setState(SpindleRotation newState) {
		this.state = newState;
	}

	public enum SpindleRotation {
		UNDEFINED,
		OFF,
		CLOCKWISE, 
		COUNTERCLOCKWISE
	}
}
