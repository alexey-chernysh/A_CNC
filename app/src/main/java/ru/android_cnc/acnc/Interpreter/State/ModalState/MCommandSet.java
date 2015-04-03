/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State.ModalState;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandTorchOff;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CCommandTorchOn;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.ProgramLoader;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public enum MCommandSet {
	M0(0, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Program stop
	M1(1, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Optional program stop
	M2(2, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){   // Program end
        @Override
        public void evaluate() throws InterpreterException {
            InterpreterState.modalState.set(modalGroup, this);
            ProgramLoader.command_sequence.prepare();
        };
    }, // Program end
	M3(3, MCommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){ // Rotate spindle clockwise
	}, 
	M4(4, MCommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){}, // Rotate spindle counterclockwise
	M5(5, MCommandModalGroupSet.M_GROUP7_SPINDLE_TURNING){}, // Stop spindle rotation
	M6(6, MCommandModalGroupSet.M_GROUP6_TOOL_CHANGE){}, // Tool change (by two macros)
	M7(7, MCommandModalGroupSet.M_GROUP8_COOLANT){ // Mist coolant on
        @Override
        public void evaluate() throws InterpreterException {
            InterpreterState.modalState.set(modalGroup, this);
            CCommandTorchOn torchOn = new CCommandTorchOn();
            ProgramLoader.command_sequence.add(torchOn);
        };
	},
	M8(8, MCommandModalGroupSet.M_GROUP8_COOLANT){ // Flood coolant on
        @Override
        public void evaluate() throws InterpreterException{
            InterpreterState.modalState.set(modalGroup, this);
            CCommandTorchOff torchOff = new CCommandTorchOff();
            ProgramLoader.command_sequence.add(torchOff);
        };
	},
	M9(9, MCommandModalGroupSet.M_GROUP8_COOLANT){ // All coolant off
	},
	M30(30, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Program end and Rewind
	M47(47, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Repeat program from first line
	M48(48, MCommandModalGroupSet.M_GROUP9_OVERRIDES){ // Enable speed and feed override
		@Override
		public void evaluate() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);		
		};
	}, 
	M49(49, MCommandModalGroupSet.M_GROUP9_OVERRIDES){ // Disable speed and feed override
		@Override
		public void evaluate() throws InterpreterException{
			InterpreterState.modalState.set(modalGroup, this);		
		};
	}, 
	M98(98, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Call subroutine
	M99(99, MCommandModalGroupSet.M_GROUP4_PROGRAM_CONTROL){}, // Return from subroutine/repeat
	MDUMMY(99999, MCommandModalGroupSet.M_GROUP0_NON_MODAL){
		@Override
		public void evaluate() throws InterpreterException{
		};
	};
	
	public int number;
	public MCommandModalGroupSet modalGroup;

	public void evaluate() throws InterpreterException{
		InterpreterState.modalState.set(modalGroup, this);
	};
	
	private MCommandSet(int n, MCommandModalGroupSet g){
		this.number = n;
		this.modalGroup = g;
	};
	
}
