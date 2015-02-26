/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.Toast;

public class CCommandDwell extends CanonCommand {

	private double delay_; // milliseconds
	
	public CCommandDwell(double d){
		super(CanonCommand.type.WAIT_STATE_CHANGE);
		this.delay_ = d;
	}

	public double getDelay() {
		return delay_;
	}

    @Override
    public void draw(Context context, Canvas canvas) {
        Toast toast = new Toast(context);
        toast.setText("Delay");
        toast.setDuration((int)delay_);
        toast.show();

        // nothing to draw
    }
}
