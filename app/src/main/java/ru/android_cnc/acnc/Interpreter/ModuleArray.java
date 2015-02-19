/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

import java.util.ArrayList;

public class ModuleArray {

	private ArrayList<ProgramModule> modules;
	
	public ModuleArray(){
		modules = new ArrayList<ProgramModule>();
	}
	
	public void add(ProgramModule nm){
		modules.add(nm);
	}
	
	public ProgramModule getByNum(int n) throws InterpreterException{
		for(int i=0; i<modules.size(); i++)
			if(modules.get(i).num == n ) return modules.get(i);
		throw new InterpreterException("Request for unknown module!");
	}

	public ProgramModule getMain() {
		return modules.get(0);
	}
	
}
