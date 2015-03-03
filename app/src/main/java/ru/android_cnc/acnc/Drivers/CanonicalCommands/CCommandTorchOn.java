/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

public class CCommandTorchOn extends CanonCommand {
	
	// cutter ON = initial positioning + ignition + perforation

	public CCommandTorchOn() {
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

    @Override
    public void execute() {

    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public String toString(){
        return "Torch ON";
    }

}
