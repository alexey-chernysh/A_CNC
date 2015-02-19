/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

public class TorchOff extends CanonCommand { 
	// cutter OFF = turn torch off + lift torch up for free motion

	public TorchOff() {
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

}
