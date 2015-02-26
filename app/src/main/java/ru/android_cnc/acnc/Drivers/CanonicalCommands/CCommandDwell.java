/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

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
        final ProgressDialog progress = new ProgressDialog(context);
        progress.setMessage("Downloading Music :) ");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();

        final int totalProgressTime = 100;
        int jumpTime = 0;
        while(jumpTime < totalProgressTime){
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                jumpTime += 1;
                progress.setProgress(jumpTime);
            }
            catch (InterruptedException ie){
                ie.printStackTrace();
            }
        }
        progress.hide();
    }
}
