/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

import ru.android_cnc.acnc.Geometry.CoordinateRotation;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.VariablesSet;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.Interpreter.State.ModalState.ModalState;

public class InterpreterState {

    public static VariablesSet vars_ = new VariablesSet();
    public static boolean IsBlockDelete = true;

	private static CNCPoint homePosition = new CNCPoint(0.0,0.0);
	private static CNCPoint lastPosition = new CNCPoint(0.0,0.0);
	private static double currentFeedRate_ = 0.0; // max velocity mm in sec

	public static ModalState modalState;

    public static CoordinateRotation coordinateRotation = CoordinateRotation.getInstance();
	public static ToolSet toolSet;
	public static Spindle spindle;
	public static FeedRate feedRate;
	public static CutterRadiusCompensation offsetMode;
	public static CutterRadiusCompensation zeroOffsetMode;
    public static ToolHeightCompensation toolHeightCompensation = ToolHeightCompensation.getInstance();

	public InterpreterState() {
		modalState = new ModalState();
        modalState.initToDefaultState();
		toolSet = new ToolSet();
		spindle = new Spindle();
		feedRate = new FeedRate();
		offsetMode = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF, 1.5);
		zeroOffsetMode = new CutterRadiusCompensation(CutterRadiusCompensation.mode.OFF, 0.0);
	}

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
	
	public static void setHomePoint(double X, double Y, double Z, double A, double B, double C) {
		InterpreterState.homePosition.setX(InterpreterState.homePosition.getX() + X);
		InterpreterState.lastPosition.setX(InterpreterState.lastPosition.getX() - X);
		InterpreterState.homePosition.setY(InterpreterState.homePosition.getY() + Y);
		InterpreterState.lastPosition.setY(InterpreterState.lastPosition.getY() - Y);
        InterpreterState.homePosition.setZ(InterpreterState.homePosition.getZ() + Z);
        InterpreterState.lastPosition.setZ(InterpreterState.lastPosition.getZ() - Z);
	}

	public static CNCPoint getLastPosition() {
		return InterpreterState.lastPosition;
	}

	public static void setLastPosition(CNCPoint newPos) {
		InterpreterState.lastPosition = newPos;
	}

}
