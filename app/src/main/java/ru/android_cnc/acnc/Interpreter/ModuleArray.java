/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

import java.util.ArrayList;

import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class ModuleArray {

	private ArrayList<ProgramModule> modules;
	
	public ModuleArray(){
		modules = new ArrayList<ProgramModule>();
	}
	
	public void add(ProgramModule nm){
		modules.add(nm);
	}
	
	public ProgramModule getByName(String name) throws EvolutionException {
		for(int i=0; i<modules.size(); i++)
			if(modules.get(i).moduleName.equals(name))
                return modules.get(i);
		throw new EvolutionException("Request for unknown module!");
	}

	public ProgramModule getMain() {
		return modules.get(0);
	}
	
}
