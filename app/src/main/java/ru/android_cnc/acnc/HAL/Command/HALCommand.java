/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.Command;

public class HALCommand {
	
	private double phase_ = 0.0; // command exevution phase from 0.0 to 1.0

	public double getPhase() {
		return phase_;
	}

	public void setPhase(double phase_) {
		this.phase_ = phase_;
	}

}
