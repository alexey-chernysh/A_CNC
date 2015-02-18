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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ProgramLoader {

	public String fileName_;
	private static ArrayList<LineLoader> lineArray;
	private static ModuleArray moduleArray;
	public static InterpreterState interpreterState;
	public static CanonCommandSequence command_sequence;
	
	public ProgramLoader(String fn) throws InterpreterException{
		
		fileName_ = fn;
		lineArray = new ArrayList<LineLoader>();
		moduleArray = new ModuleArray();
		interpreterState = new InterpreterState();
		command_sequence = new CanonCommandSequence();
		
		try{
			InputStream f = new FileInputStream(fileName_);

			BufferedReader inputStream = new BufferedReader(new InputStreamReader(f));
			String line;
			ProgramModule lastModule = null;
			boolean programEndReached = false;
			while (((line = inputStream.readLine()) != null)&&(!programEndReached)) {
				LineLoader currentBlock = new LineLoader(line);
				lineArray.add(currentBlock); 
				final int lineOrdinalNum = lineArray.size() - 1;
				if(currentBlock.isModuleStart()){
					ProgramModule newModule = new ProgramModule(currentBlock.getModuleNum(), lineArray);
					newModule.setStart(lineOrdinalNum);
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
					lastModule.setEnd(lineOrdinalNum);
				}
				System.out.println(line);
			}
			inputStream.close();
			f.close();
			if(!programEndReached) throw new InterpreterException("M2 needed in the end of program!");
			this.evalute();
		}
		catch (FileNotFoundException e){
		}
		catch (IOException e){
		}
	}

	private void evalute() {
		try {
			ProgramLoader.moduleArray.getMain().evalute();
		} catch (InterpreterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
