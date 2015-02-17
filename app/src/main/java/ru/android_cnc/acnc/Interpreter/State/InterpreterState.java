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

package ru.android_cnc.acnc.Interpreter.State;

import Interpreter.Expression.Variables.VariablesSet;
import Interpreter.InterpreterException;
import Interpreter.Motion.Point;
import Interpreter.State.ModalState.ModalState;

public class InterpreterState {

	public static VariablesSet vars_ = new VariablesSet();

	public static boolean IsBlockDelete = true;

	private static Point homePosition = new Point(0.0,0.0);
	private static Point lastPosition = new Point(0.0,0.0);
	private static double currentFeedRate_ = 0.0; // max velocity mm in sec

	public static ModalState modalState;
	
	public static ToolSet toolSet;
	public static Spindle spindle;
	public static FeedRate feedRate;
	public static CutterRadiusCompensation offsetMode;
	public static CutterRadiusCompensation zeroOffsetMode;

	public InterpreterState() throws InterpreterException{
		modalState = new ModalState();
		toolSet = new ToolSet();
		spindle = new Spindle();
		feedRate = new FeedRate();
		offsetMode = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF, 0.0);
		zeroOffsetMode = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF, 0.0);
		modalState.initToDefaultState();
	};

	public static double getCurrentFeedRate() {
		return InterpreterState.currentFeedRate_;
	}
	
	public void setCurrentFeedRate(double currentFeedRate) {
		InterpreterState.currentFeedRate_ = currentFeedRate;
	}
	
	public static double getHomePointX() {
		return InterpreterState.homePosition.getX();
	}
	
	public static double getHomePointY() {
		return InterpreterState.homePosition.getY();
	}
	
	public static void setHomePoint(double X, double Y) {
		InterpreterState.homePosition.setX(InterpreterState.homePosition.getX() + X);
		InterpreterState.lastPosition.setX(InterpreterState.lastPosition.getX() - X);
		InterpreterState.homePosition.setY(InterpreterState.homePosition.getY() + Y);
		InterpreterState.lastPosition.setY(InterpreterState.lastPosition.getY() - Y);
	}

	public static Point getLastPosition() {
		return InterpreterState.lastPosition;
	}

	public static void setLastPosition(Point newPos) {
		InterpreterState.lastPosition = newPos;
	}

}
