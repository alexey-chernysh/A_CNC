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

package ru.android_cnc.acnc.HAL;

import java.util.ArrayList;

import HAL.Command.HALCommand;
import HAL.ExternalEventController.ExternalEventController;
import HAL.InvertorController.InvertorController;
import HAL.MotionController.MotionController;
import HAL.TorchHeightController.TorchHeightController;
import Interpreter.Motion.Point;

public class HALMashine implements Runnable {

	private ProgramState programState = ProgramState.PAUSED;
	private CutterState cutterState = CutterState.UNDEFINED;
	public static ArrayList<HALCommand> commands_ = new ArrayList<HALCommand>();
	
	public static MotionController MC;
	Thread threadMC;

	public static InvertorController IC;
	Thread threadIC;
	
	public static TorchHeightController THC;
	Thread threadTHC;
	
	public static ExternalEventController EEC;
	Thread threadEEC;
	
	public HALMashine(){
		 MC = new MotionController();
		 threadMC = new Thread(MC);
		 threadMC.start();
		 
		 IC = new InvertorController();
		 threadIC = new Thread(IC);
		 threadIC.start();
		 
		 THC = new TorchHeightController();
		 threadTHC = new Thread(THC);
		 threadTHC.start();
		 
		 EEC = new ExternalEventController();
		 threadEEC = new Thread(EEC);
		 threadEEC.start();
		 
	}
	
	public static void loadNewProgram(ArrayList<HALCommand> commands){
		commands_ = commands;
	}
	
	Point positionBeforeJog = null;
	private final double manualTorchHeightChange = 1.0;
	private JoggingStateX joggingStateX = JoggingStateX.NONE;
	private JoggingStateY joggingStateY = JoggingStateY.NONE;

	@Override
	public void run() {
        if(EEC.torchUpPressed()) THC.torchUp(manualTorchHeightChange);
		if(EEC.torchDownPressed()) THC.torchDown(manualTorchHeightChange);
		switch (programState){
		case PAUSED:
			controlInPause();
			break;
		case STARTING:
			starting();
			break;
		case RUNNING:
			controlOnTheRun();
			break;
		case STOPPING:
			stopping();
			break;
		case JOG:
			controlInJogMode();
			break;
		default:
		}
		reDraw();
	}
	
	private void controlInPause(){
		if(EEC.jogPressed()){
			positionBeforeJog = MC.getCurrentPosition();
			programState = ProgramState.JOG;
		} 
		if(EEC.isStartPressed()){
			if(EEC.noSignificantAlarms()){
				if(IC.isReady()){
        			programState = ProgramState.STARTING;
				}
			}
		}
	}

	private void starting(){
		// TODO check in which phase program was paused and start according this state
		switch(cutterState){
		case UNDEFINED: // program is in the start
			break;
		case PERFORATION: // program paused in perforation
			break;
		case FREE_RUN: // program paused on free run
			break;
		case WORK_RUN: // program paused on cutting
			break;
		default:
		}
	}
	
	private void controlOnTheRun(){
		if(EEC.isStopPressed()){
   			programState = ProgramState.STOPPING;
		}
	}
	
	private void stopping(){
		
	}
	
	private void controlInJogMode(){
		if(EEC.isJogLeftPressed())
			switch(joggingStateX){
			case NONE:
				// TODO  start motion left
				joggingStateX = JoggingStateX.LEFT;
				break;
			case LEFT:
			case RIGHT:
				    // TODO  stop motion
				joggingStateX = JoggingStateX.NONE;
				break;
			}
		if(EEC.isJogRightPressed())
			switch(joggingStateX){
			case NONE:
				// TODO  start motion right
				joggingStateX = JoggingStateX.RIGHT;
				break;
			case LEFT:
			case RIGHT:
				    // TODO  stop motion
				joggingStateX = JoggingStateX.NONE;
				break;
			}
		if(EEC.isJogForwardPressed())
			switch(joggingStateY){
			case NONE:
				// TODO  start motion forward
				joggingStateY = JoggingStateY.FORWARD;
				break;
			case FORWARD:
			case BACKWARD:
				    // TODO  stop motion
				joggingStateY = JoggingStateY.NONE;
				break;
			}
		if(EEC.isJogBackwardPressed())
			switch(joggingStateY){
			case NONE:
				// TODO  start motion backward
				joggingStateY = JoggingStateY.BACKWARD;
				break;
			case FORWARD:
			case BACKWARD:
				    // TODO  stop motion
				joggingStateY = JoggingStateY.NONE;
				break;
			}
	}
	
	private void reDraw(){
	}
	
    private enum ProgramState{
    	PAUSED,
    	STARTING,
    	RUNNING,
    	STOPPING,
    	JOG
    }	

	public enum CutterState{
		UNDEFINED,
		PERFORATION,
		FREE_RUN,
		WORK_RUN
	}

	private enum JoggingStateX{
		NONE,
		LEFT,
		RIGHT
	}

	private enum JoggingStateY{
		NONE,
		FORWARD,
		BACKWARD
	}

}
