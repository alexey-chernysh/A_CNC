/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Drivers;

import android.view.View;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public interface GeneralDriver {
	
	void load(CanonCommandSequence sourceCommands) throws InterpreterException;
	
	void start(View v);
	
	void pause();
	
	void resume();
	
	void rewind();
	
	void forewind();

}
