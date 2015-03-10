/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

import android.graphics.Canvas;

import java.util.concurrent.TimeUnit;

import ru.android_cnc.acnc.HAL.HALMashine;

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
    public void execute() {
/*
        switch(HALMashine.getMode()){
            case VIEW:
                try {
                    int d = (int)(delay_ * HALMashine.getViewTimeScale());
                    TimeUnit.MILLISECONDS.sleep(d);
                    //TODO GUI link needed
                }
                catch (InterruptedException ie){
                    ie.printStackTrace();
                }
                break;
            case DEMO:
                try {
                    int d = (int)(delay_ * HALMashine.getDemoTimeScale());
                    TimeUnit.MILLISECONDS.sleep(d);
                    //TODO GUI link needed
                }
                catch (InterruptedException ie){
                    ie.printStackTrace();
                }
                break;
            case WORK:
                try {
                    TimeUnit.MILLISECONDS.sleep((int)delay_);
                    //TODO GUI link needed
                }
                catch (InterruptedException ie){
                        ie.printStackTrace();
                }
                break;
            default:
        }
         */
    }

    @Override
    public void draw(Canvas canvas) {
/*
        final ProgressDialog progress = new ProgressDialog(context.getViewContext());
        progress.setMessage("Dwell: ");
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
*/
    }
}
