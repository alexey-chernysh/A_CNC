/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.TorchHeightController;

import android.graphics.Canvas;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommand;

public class THCCommand extends CanonCommand {
	
	private THCState state_;

	public THCCommand() {
		super(type.CHANGE_STATE);
		state_ = THCState.UNDEFINED;
	}

	public THCState getState() {
		return state_;
	}

	public void setState(THCState state_) {
		this.state_ = state_;
	}

	@Override
	public void execute() {

	}

	@Override
	public void draw(Canvas canvas) {

	}

	public enum THCState {
		UNDEFINED,
		ON,
		OFF
	}
}
