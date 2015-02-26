/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;

public class TorchOn extends CanonCommand {
	
	// cutter ON = initial positioning + ignition + perforation

	public TorchOn() { 
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

    @Override
    public void draw(Context context, Canvas canvas) {

    }
}
