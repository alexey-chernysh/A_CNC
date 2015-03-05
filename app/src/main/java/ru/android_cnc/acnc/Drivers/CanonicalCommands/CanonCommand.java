/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.Draw.DrawableObjectLimits;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

public abstract class CanonCommand {
	
	private type type_ = type.UNDEFINED;

	public CanonCommand(type t){
		setType(t);
	}

    abstract public void execute();
    abstract public void draw(Canvas canvas);

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
		WAIT_STATE_CHANGE,
        MESSAGE
	}

}
