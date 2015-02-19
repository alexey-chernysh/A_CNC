/*
 * @author Alexey Chernysh
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
