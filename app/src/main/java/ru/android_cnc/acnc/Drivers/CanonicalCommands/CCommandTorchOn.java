/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.GraphView.CNCViewContext;

public class CCommandTorchOn extends CanonCommand {
	
	// cutter ON = initial positioning + ignition + perforation

	public CCommandTorchOn() {
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

    @Override
    public void execute() {

    }

    @Override
    public void draw(CNCViewContext context, Canvas canvas) {

    }
}
