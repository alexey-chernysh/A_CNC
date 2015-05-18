/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

public abstract class CanonCommand {
	
	private type type_ = type.UNDEFINED;

	public CanonCommand(type t){
		setType(t);
	}

    abstract public void execute();
    abstract public void draw(Canvas canvas);

	public type getType() { return this.type_; }

	public void setType(type t) {
		this.type_ = t;
	}

	public enum type{
		UNDEFINED,
		MOTION,
		CHANGE_STATE,
		WAIT_STATE_CHANGE,
        MESSAGE
	}

}
