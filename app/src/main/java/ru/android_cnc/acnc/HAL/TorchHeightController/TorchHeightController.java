/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.TorchHeightController;

public class TorchHeightController implements Runnable {
	
	private boolean inCollision = false;
	
	public double arcVoltageSetting_ = 100.0;
	private double mesuredVoltage_ = 0.0;
	private static final double minArcVoltage = 50.0;
	private static final double maxArcVoltage = 200.0;
	
	public double initialHeight_ = 3.0;
	private double IP_delta = 1.0;
	
	public double feedRateSetting_ = 2000.0;
	public double currentFeedRate_ = 0.0;
	public double feedRateTheshold_ = 0.2;
	
	public boolean keyUpPressed = false;
	public boolean keyDownPressed = false;
	private double key_pressed_delta = 1.0;
	
	private THCstate currentState = THCstate.MANUAL;
	
	public enum THCstate {
		AUTO,
		MANUAL,
		INITIAL_POSITIONING;
	}

	@Override
	public void run() {
		
		boolean doRun = true;
		while (doRun){

			if(keyUpPressed){
				torchUp(key_pressed_delta);
			} else {
				if(keyDownPressed){
					torchDown(key_pressed_delta);
				} else {
					
				}
			}

			this.mesuredVoltage_ = mesureVoltage();

			switch (this.currentState){
			case AUTO: {				
				boolean inThreshold = 
						(Math.abs((this.feedRateSetting_ - this.currentFeedRate_)/this.feedRateSetting_) <= this.feedRateTheshold_);
				if(inThreshold && (!keyUpPressed) && (!keyDownPressed))
				if(this.mesuredVoltage_ >= minArcVoltage)
				if(this.mesuredVoltage_ <= maxArcVoltage){
					double delta = this.mesuredVoltage_ - this.arcVoltageSetting_;
					if(delta > 0.0) torchUp(delta);
					if(delta < 0.0) torchDown(delta);
				}
			}
			break;
			case MANUAL:
			break;
			case INITIAL_POSITIONING: {
				boolean IP_success = false;
				do{
					IP_success = contactReached();
					torchDown(this.IP_delta);
				} while ((this.currentState == THCstate.INITIAL_POSITIONING) // somethine go wrong and operator cancel positioning
						  || IP_success); // or metal plate reached 
				torchUp(this.initialHeight_);
			}
			break;
			default:
			}
		};
	}
	
	private boolean contactReached() {
		// TODO Auto-generated method stub
		return inCollision;
	}
	
	private double mesureVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void torchUp(double delta_height) { // mm
		
	}

	public void torchDown(double delta_height) { // mm
		
	}
	
	public double getArcVoltage(){
		return this.mesuredVoltage_;
	}
	
	public void setState(THCstate newState){
		synchronized(this.currentState){
			this.currentState = newState;
		}
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

}
