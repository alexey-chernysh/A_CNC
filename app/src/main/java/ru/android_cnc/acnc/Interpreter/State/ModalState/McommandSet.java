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

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import Drivers.CanonicalCommands.TorchOff;
import Drivers.CanonicalCommands.TorchOn;
import Interpreter.InterpreterException;
import Interpreter.ProgramLoader;
import Interpreter.State.InterpreterState;

public enum McommandSet {
	M0(0, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Program stop
	M1(1, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Optional program stop
	M2(2, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Program end
	M3(3, McommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){ // Rotate spindle clockwise
	}, 
	M4(4, McommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){}, // Rotate spindle counterclockwise
	M5(5, McommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){}, // Stop spindle rotation
	M6(6, McommandModalGroupSet.M_GROUP6_TOOL_CHANGE){}, // Tool change (by two macros)
	M7(7, McommandModalGroupSet.M_GROUP8_COOLANT){ // Mist coolant on
	}, 
	M8(8, McommandModalGroupSet.M_GROUP8_COOLANT){ // Flood coolant on
		@Override
		public void evalute() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);	
			TorchOn torchOn = new TorchOn();
			ProgramLoader.command_sequence.add(torchOn);
		};
	}, 
	M9(9, McommandModalGroupSet.M_GROUP8_COOLANT){ // All coolant off
		@Override
		public void evalute() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);	
			TorchOff torchOff = new TorchOff();
			ProgramLoader.command_sequence.add(torchOff);
		};
	}, 
	M30(30, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Program end and Rewind
	M47(47, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Repeat program from first line
	M48(48, McommandModalGroupSet.M_GROUP9_OVERRIDES){ // Enable speed and feed override
		@Override
		public void evalute() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);		
		};
	}, 
	M49(49, McommandModalGroupSet.M_GROUP9_OVERRIDES){ // Disable speed and feed override
		@Override
		public void evalute() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);		
		};
	}, 
	M98(98, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Call subroutine
	M99(99, McommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Return from subroutine/repeat
	MDUMMY(99999,McommandModalGroupSet.M_GROUP0_NON_MODAL){
		@Override
		public void evalute() throws InterpreterException{
		};
	};
	
	public int number;
	public McommandModalGroupSet modalGroup;

	public void evalute() throws InterpreterException{
		InterpreterState.modalState.set(modalGroup, this);
	};
	
	private McommandSet(int n, McommandModalGroupSet g){
		this.number = n;
		this.modalGroup = g;
	};
	
}
