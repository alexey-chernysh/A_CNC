/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;

public class CCommandTorchOff extends CanonCommand {
	// cutter OFF = turn torch off + lift torch up for free motion

	public CCommandTorchOff() {
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

    @Override
    public void draw(Context context, Canvas canvas) {

    }
}