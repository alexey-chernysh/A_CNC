/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

public enum McommandModalGroupSet {
	M_GROUP4_PROGRAM_CONTROL,  // M0, M1, M2, M30 stopping
	M_GROUP6_TOOL_CHANGE,  // M6 tool change
	M_GROUP7_SPINDLE_TURNING,  // M3, M4, M5 spindle turning
	M_GROUP8_COOLANT,  // M7, M8, M9 coolant (special case: M7 and M8 may be active at the same time)
	M_GROUP9_OVERRIDES,  // M48, M49 enable/disable feed and speed override controls
	M_GROUP0_NON_MODAL;
}
