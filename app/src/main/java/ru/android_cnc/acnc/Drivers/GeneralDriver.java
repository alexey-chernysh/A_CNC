/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers;

import java.util.ArrayList;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommand;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

public interface GeneralDriver {
	
	void loadProgram(CanonCommandSequence sourceCommands) throws InterpreterException;
	
	void startProgram(); 
	
	void pauseProgram();
	
	void resumeProgram();
	
	void rewindProgram();
	
	void forewindProgram();

}
