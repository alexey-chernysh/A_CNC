/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.ExternalEventController;

public class ExternalEventController implements Runnable {
	
	private boolean leftLimitReached = false;
	private boolean rightLimitReached = false;
	private boolean bottomLimitReached = false;
	private boolean topLimitReached = false;

	private boolean startPressed = false;
	private boolean stopPressed = false;

	private boolean jogLeftPressed = false;
	private boolean jogRightPressed = false;
	private boolean jogForwardPressed = false;
	private boolean jogBackwardPressed = false;

	
	@Override
	public void run() {
		boolean doRun = true;
		while (doRun){
		}
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public boolean torchUpPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean torchDownPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStartPressed() {
		return startPressed;
	}

	public boolean isStopPressed() {
		return stopPressed;
	}

	public boolean noSignificantAlarms() {
		return (this.leftLimitReached
			  ||this.rightLimitReached
			  ||this.bottomLimitReached
			  ||this.topLimitReached);
	}

	public boolean isJogLeftPressed() {
		return jogLeftPressed;
	}

	public boolean isJogRightPressed() {
		return jogRightPressed;
	}

	public boolean isJogForwardPressed() {
		return jogForwardPressed;
	}

	public boolean isJogBackwardPressed() {
		return jogBackwardPressed;
	}

	public boolean jogPressed() {
		return (this.jogLeftPressed
			  ||this.jogRightPressed
			  ||this.jogForwardPressed
			  ||this.jogBackwardPressed);
	}

}
