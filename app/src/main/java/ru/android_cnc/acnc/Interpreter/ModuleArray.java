/*
 * Copyright 2014-2015 Alexey Chernysh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
