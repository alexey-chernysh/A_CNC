/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import ru.android_cnc.acnc.GraphView.CNCViewContext;

public class CCommandTorchOff extends CanonCommand {
	// cutter OFF = turn torch off + lift torch up for free motion

	public CCommandTorchOff() {
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

    @Override
    public void execute() {

    }

    @Override
    public void draw(CNCViewContext context, Canvas canvas) {

    }
}
