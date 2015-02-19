/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

public class G04 extends CanonCommand {

	private double delay_; // milliseconds
	
	public G04(double d){
		super(CanonCommand.type.WAIT_STATE_CHANGE);
		this.delay_ = d;
	}

	public double getDelay() {
		return delay_;
	}

}
