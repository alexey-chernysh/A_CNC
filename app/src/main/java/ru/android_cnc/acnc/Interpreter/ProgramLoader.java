/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

import android.text.Spannable;
import android.text.SpannableString;
import java.util.ArrayList;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ProgramLoader {

	private static ArrayList<LineLoader> lineArray;
	private static ModuleArray moduleArray;
	public static InterpreterState interpreterState;
	public static CanonCommandSequence command_sequence;
	
    public static Spannable load(String source) {

        lineArray = new ArrayList<LineLoader>();
        moduleArray = new ModuleArray();
        interpreterState = new InterpreterState();
        command_sequence = new CanonCommandSequence();

        Spannable spannedSource = new SpannableString(source);
        final String separator = System.getProperty("line.separator");
        final int separatorLength = separator.length();
        String[] line = source.split(separator);
        final int n_lines = line.length;

        ProgramModule lastModule = null;
        boolean programEndReached = false;
        int currentStringStart = 0;
        int currentStringLength = 0;
        int currentStringStartOffset = 0;
        for(int i=0;i<n_lines;i++){
            currentStringLength = line[i].length();
            currentStringStartOffset += separatorLength + currentStringLength;
            try {
                LineLoader currentBlock = null;
                currentBlock = new LineLoader(line[i]);
                lineArray.add(currentBlock);
                if(currentBlock.isModuleStart()){
                    ProgramModule newModule = new ProgramModule(currentBlock.getModuleNum(), lineArray);
                    newModule.setStart(i);
                    moduleArray.add(newModule);
                    lastModule = newModule;
                };
                if(currentBlock.isProgramEnd()){
                    programEndReached = true;
                    if(lastModule == null){
                        ProgramModule newModule = new ProgramModule(1, lineArray);
                        newModule.setStart(0);
                        moduleArray.add(newModule);
                        lastModule = newModule;
                    };
                    lastModule.setEnd(i);
                }
                currentBlock.setAllSpan(spannedSource, currentStringStart);
                currentStringStart += currentStringLength + separatorLength;
            } catch (InterpreterException e) {
                e.printStackTrace();
            }
        }
//        if(!programEndReached) throw new InterpreterException("M2 needed in the end of program!",0);
        return spannedSource;
    }

	public void evaluate() {
        command_sequence = new CanonCommandSequence();
		try {
			ProgramLoader.moduleArray.getMain().evaluate();
		} catch (EvolutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
