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

package ru.android_cnc.acnc.Drivers.Cutter;

import java.util.ArrayList;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommand;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.G00_G01;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.G02_G03;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.MotionMode;
import ru.android_cnc.acnc.Drivers.GeneralDriver;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Settings.Settings;

public class CutterDriver implements GeneralDriver {
	
	private ArrayList<CanonCommand> commands_ = null;

	public CutterDriver(){
	}
	
	
	@Override
	public void loadProgram(ArrayList<CanonCommand> sourceCommands) throws InterpreterException {
		
		commands_ = sourceCommands;
		buildVelocityProfile();
	}

	private void buildVelocityProfile() throws InterpreterException {

		int size = this.commands_.size();
		
		double perforationVel = Settings.getPerforationSpeed();
		double perforationVelDur = Settings.getPerfSpeedDuration();
		double perfLength = perforationVel*perforationVelDur/60.0; // in mm
		
		double startVel = Settings.getStartSpeed();
		double cutVel = Settings.getWorkSpeed();
		double accel = Settings.getAcceleration();
		
		for(int i=0; i<size; i++){
			Object currentCommand = this.commands_.get(i);
			boolean itsCuttingLine = currentCommand instanceof G00_G01;
			boolean itsCuttingArc = currentCommand instanceof G02_G03;
			if(itsCuttingLine || itsCuttingArc){ 
				// working with cutting lines and arc only
				Object prevCuttingCommand = PreviousCutting(i);
				Object nextCuttingCommand = NextCutting(i);
				double neededVelocity = ((G00_G01)currentCommand).getVelocityPlan().getStartVel();
				if(prevCuttingCommand == null){ 
					// first cutting after perforation - inserting slow perforation phase 
					if(itsCuttingLine){
						G00_G01 currentLine = (G00_G01)currentCommand;
						double currentLength = currentLine.length();
						if((perfLength < currentLength)&&(perfLength > 0.0)){
							G00_G01 newLine1 = currentLine.newSubLine(0, perfLength);
							newLine1.setVelocityProfile(perforationVel,startVel);
							G00_G01 newLine2 = currentLine.newSubLine(perfLength, currentLength);
							newLine1.setVelocityProfile(startVel, neededVelocity);
							this.commands_.remove(i);
							this.commands_.add(i, newLine2);
							this.commands_.add(i, newLine1);
							i++;
						} else {
							currentLine.setVelocityProfile(startVel, neededVelocity);
						}
					} else {
						if(itsCuttingArc){
							G02_G03 currentArc = (G02_G03)currentCommand;
							double currentLength = currentArc.length();
							if((perfLength < currentLength)&&(perfLength > 0.0)){
								G02_G03 newArc1 = currentArc.newSubArc(0, perfLength);
								newArc1.setVelocityProfile(perforationVel,startVel);
								G02_G03 newArc2 = currentArc.newSubArc(perfLength, currentLength);
								newArc1.setVelocityProfile(startVel, neededVelocity);
								this.commands_.remove(i);
								this.commands_.add(i, newArc2);
								this.commands_.add(i, newArc1);
								i++;
							} else {
								currentArc.setVelocityProfile(startVel, neededVelocity);
							}
						}				
					}
				} else {
					// its no first cutting line - adjustment needed
					G00_G01 beforeLine = (G00_G01)prevCuttingCommand;
					double angleBeforeStart = beforeLine.getEndTangentAngle();
					double velBeforeStart = beforeLine.getVelocityPlan().getEndVel();
					G00_G01 currentLine = (G00_G01)currentCommand;
					double angleStart = currentLine.getStartTangentAngle();
					if(Math.abs(angleStart - angleBeforeStart) < Settings.angleTol){
						// fine case of smooth line angle adjustment.
						// adjust velocity now
						if(velBeforeStart == neededVelocity){
							// velocity is equal
							currentLine.setVelocityProfile(neededVelocity, neededVelocity);
						} else {
							currentLine.setVelocityProfile(velBeforeStart, neededVelocity);
						}
					} else {
						currentLine.setVelocityProfile(startVel, neededVelocity);
						((G00_G01)prevCuttingCommand).getVelocityPlan().setEndVel(startVel);
					} 
				}
				if(nextCuttingCommand == null){
					((G00_G01)this.commands_.get(i)).getVelocityPlan().setEndVel(startVel);
				}

			};
		}
	}

	private Object NextCutting(int i) {
		if((i+1) >= this.commands_.size()) return null;
		Object next = this.commands_.get(i+1);
		if(next instanceof G00_G01)
			if(((G00_G01)next).getMode() == MotionMode.WORK) return next;
		if(next instanceof G02_G03) return next;
		return null;
	}

	private Object PreviousCutting(int i){  // command index
		if(i <= 0) return null;
		Object before = this.commands_.get(i-1);
		if(before instanceof G00_G01)
			if(((G00_G01)before).getMode() == MotionMode.WORK) return before;
		if(before instanceof G02_G03) return before;
		return null;
	} 

	@Override
	public void startProgram() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pauseProgram() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resumeProgram() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rewindProgram() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forewindProgram() {
		// TODO Auto-generated method stub
		
	}

}
