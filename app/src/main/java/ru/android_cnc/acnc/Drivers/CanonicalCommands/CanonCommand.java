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

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import ru.android_cnc.acnc.Interpreter.InterpreterException;

public class CanonCommand {
	
	private type type_ = type.UNDEFINED;
	
	public CanonCommand(type t){
		setType(t);
	}
	
	public void draw(){
		
	}
	
	public type getType() throws InterpreterException {
		if(type_ != type.UNDEFINED)	return type_;
		else throw new InterpreterException("Request to not initialized field");
	}

	public void setType(type t) {
		this.type_ = t;
	}

	public enum type{
		UNDEFINED,
		MOTION,
		WAIT_STATE_CHANGE
	}

}
