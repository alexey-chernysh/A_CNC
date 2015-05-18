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

import android.os.Handler;
import android.view.View;

import ru.android_cnc.acnc.HAL.MotionController.CCommandStraightLine;
import ru.android_cnc.acnc.HAL.MotionController.CCommandArcLine;
import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.HAL.MotionController.MotionMode;
import ru.android_cnc.acnc.Drivers.GeneralDriver;
import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Settings.Settings;

public class CutterDriver implements GeneralDriver {
	
	private CanonCommandSequence commands_ = null;

	public CutterDriver(){
	}
	
	
	@Override
	public void load(CanonCommandSequence sourceCommands) throws InterpreterException {
		
		commands_ = sourceCommands;
//		buildVelocityProfile();
	}

	private void buildVelocityProfile() throws EvolutionException {

		int size = this.commands_.size();
		
		double perforationVel = Settings.getPerforationSpeed();
		double perforationVelDur = Settings.getPerfSpeedDuration();
		double perfLength = perforationVel*perforationVelDur/60.0; // in mm
		
		double startVel = Settings.getStartSpeed();
		double cutVel = Settings.getWorkSpeed();
		double accel = Settings.getAcceleration();
		
		for(int i=0; i<size; i++){
			Object currentCommand = this.commands_.get(i);
			boolean itsCuttingLine = currentCommand instanceof CCommandStraightLine;
			boolean itsCuttingArc = currentCommand instanceof CCommandArcLine;
			if(itsCuttingLine || itsCuttingArc){ 
				// working with cutting lines and arc only
				Object prevCuttingCommand = PreviousCutting(i);
				Object nextCuttingCommand = NextCutting(i);
				double neededVelocity = ((CCommandStraightLine)currentCommand).getVelocityPlan().getStartVel();
				if(prevCuttingCommand == null){ 
					// first cutting after perforation - inserting slow perforation phase 
					if(itsCuttingLine){
						CCommandStraightLine currentLine = (CCommandStraightLine)currentCommand;
						double currentLength = currentLine.length();
						if((perfLength < currentLength)&&(perfLength > 0.0)){
							CCommandStraightLine newLine1 = currentLine.newSubLine(0, perfLength);
							newLine1.setVelocityProfile(perforationVel,startVel);
							CCommandStraightLine newLine2 = currentLine.newSubLine(perfLength, currentLength);
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
							CCommandArcLine currentArc = (CCommandArcLine)currentCommand;
							double currentLength = currentArc.length();
							if((perfLength < currentLength)&&(perfLength > 0.0)){
								CCommandArcLine newArc1 = currentArc.newSubArc(0, perfLength);
								newArc1.setVelocityProfile(perforationVel,startVel);
								CCommandArcLine newArc2 = currentArc.newSubArc(perfLength, currentLength);
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
					CCommandStraightLine beforeLine = (CCommandStraightLine)prevCuttingCommand;
					double angleBeforeStart = beforeLine.getEndTangentAngle();
					double velBeforeStart = beforeLine.getVelocityPlan().getEndVel();
					CCommandStraightLine currentLine = (CCommandStraightLine)currentCommand;
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
						((CCommandStraightLine)prevCuttingCommand).getVelocityPlan().setEndVel(startVel);
					} 
				}
				if(nextCuttingCommand == null){
					((CCommandStraightLine)this.commands_.get(i)).getVelocityPlan().setEndVel(startVel);
				}

			};
		}
	}

	private Object NextCutting(int i) {
		if((i+1) >= this.commands_.size()) return null;
		Object next = this.commands_.get(i+1);
		if(next instanceof CCommandStraightLine)
			if(((CCommandStraightLine)next).getMode() == MotionMode.WORK) return next;
		if(next instanceof CCommandArcLine) return next;
		return null;
	}

	private Object PreviousCutting(int i){  // command index
		if(i <= 0) return null;
		Object before = this.commands_.get(i-1);
		if(before instanceof CCommandStraightLine)
			if(((CCommandStraightLine)before).getMode() == MotionMode.WORK) return before;
		if(before instanceof CCommandArcLine) return before;
		return null;
	}

    Thread executionThread = null;
    Handler mHandler = new Handler();
    boolean paused = false;

	@Override
	public void start(View v) {
        final View view = v;
        if(executionThread == null){
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    view.postInvalidate();
                    mHandler.postDelayed(this, 100);
                }
            };
            mHandler.post(runnable);
            executionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int n = commands_.size();
                    for(int i=0; i<n; i++)commands_.get(i).execute();
                    mHandler.removeCallbacks(runnable);
                    view.postInvalidate();
                }
            });
        } else if(paused)this.resume();
        executionThread.start(); // запускаем
	}

	@Override
	public void pause() {
        try {
            executionThread.sleep(Long.MAX_VALUE);
            paused = true;
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

	@Override
	public void resume() {
        if(!executionThread.isInterrupted()) {
            executionThread.interrupt();
            paused = false;
        }
	}

	@Override
	public void rewind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forewind() {
		// TODO Auto-generated method stub
		
	}

}
