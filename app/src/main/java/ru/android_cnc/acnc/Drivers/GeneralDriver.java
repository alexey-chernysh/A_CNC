/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers;

import android.view.View;

import java.util.ArrayList;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommand;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

public interface GeneralDriver {
	
	void loadProgram(CanonCommandSequence sourceCommands) throws InterpreterException;
	
	void startProgram(View v);
	
	void pauseProgram();
	
	void resumeProgram();
	
	void rewindProgram();
	
	void forewindProgram();

}
