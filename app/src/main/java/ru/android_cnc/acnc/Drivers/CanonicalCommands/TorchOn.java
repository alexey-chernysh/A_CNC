/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers.CanonicalCommands;

public class TorchOn extends CanonCommand { 
	
	// cutter ON = initial positioning + ignition + perforation

	public TorchOn() { 
		super(CanonCommand.type.WAIT_STATE_CHANGE);
	}

}
