/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.Overrides;

public class OverrideSwitch {

	private boolean override_ = false; 
	private boolean overrideEnabled_ = false;

	public boolean isOverride() {
		return override_;
	}

	public void setOverride(boolean override) {
		this.override_ = override;
	}

	public boolean OverrideEnabled() {
		return overrideEnabled_;
	}

	public void enableOverride() {
		this.overrideEnabled_ = true;
	}

	public void disableOverride() {
		this.overrideEnabled_ = false;
	}

	public enum Overrides{
		ON, 
		OFF,
		UNDEFINED;
	}
	
}
