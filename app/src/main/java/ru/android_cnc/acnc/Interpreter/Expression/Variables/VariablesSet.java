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

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.Motion.CNCPoint;

public class VariablesSet {
	
	private static final int G28HomePos_ = 5160;
	private static final int G30HomePos_ = 5180;
	private static final int ScalePos_ = 5190;
	private static final int G92OffsetPos_ = 5210;
	private static final int workOffsetsPos_ = 5220;
	private static final int shift_ = 20;
	private VarArray va = new VarArray();
	public static final int maxToolNumber = 254;
	
	public VariablesSet(){
	}
	
	public double get(int num) throws InterpreterException{
		return this.va.get(num);
	}
	
	public void set(int num, double value) throws InterpreterException{
		this.va.set(num, value);
	}
	
	private static final int offset_X = 1;
	private static final int offset_Y = 2;
	private static final int offset_Z = 3;
	private static final int offset_A = 4;
	private static final int offset_B = 5;
	private static final int offset_C = 6;
	
	private void setX(int base, double value) throws InterpreterException{
		this.va.set(base + offset_X, value);
	}
	private void setY(int base, double value) throws InterpreterException{
		this.va.set(base + offset_Y, value);
	}
	private void setZ(int base, double value) throws InterpreterException{
		this.va.set(base + offset_Z, value);
	}
	private void setA(int base, double value) throws InterpreterException{
		this.va.set(base + offset_A, value);
	}
	private void setB(int base, double value) throws InterpreterException{
		this.va.set(base + offset_B, value);
	} 
	private void setC(int base, double value) throws InterpreterException{
		this.va.set(base + offset_C, value);
	}
	
	private double getX(int base) throws InterpreterException{
		return this.va.get(base + offset_X);
	}
	private double getY(int base) throws InterpreterException{
		return this.va.get(base + offset_Y);
	}
	private double getZ(int base) throws InterpreterException{
		return this.va.get(base + offset_Z);
	}
	private double getA(int base) throws InterpreterException{
		return this.va.get(base + offset_A);
	}
	private double getB(int base) throws InterpreterException{
		return this.va.get(base + offset_B);
	}
	private double getC(int base) throws InterpreterException{
		return this.va.get(base + offset_C);
	}

	public void setToolFixtureOffset(int L, TokenParameter tp, double value) throws InterpreterException{
		int varPosition = workOffsetsPos_ + (L-1)*shift_;
		switch(tp){
		case X:
			this.setX(varPosition, value);
			break;
		case Y:
			this.setY(varPosition, value);
			break;
		case Z:
			this.setZ(varPosition, value);
			break;
		case A:
			this.setA(varPosition, value);
			break;
		case B:
			this.setB(varPosition, value);
			break;
		case C:
			this.setC(varPosition, value);
			break;
		default:
			throw new InterpreterException("Illegal alfa word (" + tp.toString() + ") in G10");
		}
	}
	
	public void setWorkingToolOffset(int P, double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
		if((P>0)&(P<=VariablesSet.maxToolNumber)){
			int varPosition = workOffsetsPos_ + (P-1)*shift_;
			this.setX(varPosition, X);
			this.setY(varPosition, Y);
			this.setZ(varPosition, Z);
			this.setA(varPosition, A);
			this.setB(varPosition, B);
			this.setC(varPosition, C);
		}
	}
	
	public void setScale(double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
			int varPosition = ScalePos_;
			this.setX(varPosition, X);
			this.setY(varPosition, Y);
			this.setZ(varPosition, Z);
			this.setA(varPosition, A);
			this.setB(varPosition, B);
			this.setC(varPosition, C);
	}
	
	public double getCurrentScaleX() throws InterpreterException{
		return this.getX(ScalePos_);
	}
	public double getCurrentScaleY() throws InterpreterException{
		return this.getY(ScalePos_);
	}
	public double getCurrentScaleZ() throws InterpreterException{
		return this.getZ(ScalePos_);
	}
	
	public boolean scalesAreEquals() throws InterpreterException{
		double sx = getCurrentScaleX();
		double sy = getCurrentScaleX();
		double sz = getCurrentScaleZ();
		return ((sx==sy)&&(sy==sz)&&(sx==sz));
	}
	
	public void setG92Offset(double X, double Y, double Z, double A, double B, double C) throws InterpreterException{
		int varPosition = G92OffsetPos_;
		this.setX(varPosition, X);
		this.setY(varPosition, Y);
		this.setZ(varPosition, Z);
		this.setA(varPosition, A);
		this.setB(varPosition, B);
		this.setC(varPosition, C);
}

	public void setCurrentWorkOffsetNum(int P) throws InterpreterException{
		this.va.set(VariablesSet.workOffsetsPos_, P);
	}
	
	private int getCurrentWorkOffsetNum() throws InterpreterException{
		return (int)this.va.get(VariablesSet.workOffsetsPos_);
	}
	
	public double getOffsetX(int i) throws InterpreterException{
		if(i>0)	return this.getX(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetX() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetX(i);
	}
	
	public double getOffsetY(int i) throws InterpreterException{
		if(i>0)	return this.getY(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetY() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetY(i);
	}
	
	public double getOffsetZ(int i) throws InterpreterException{
		if(i>0)	return this.getZ(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetZ() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetZ(i);
	}
	
	public double getOffsetA(int i) throws InterpreterException{
		if(i>0)	return this.getA(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetA() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetA(i);
	}
	
	public double getOffsetB(int i) throws InterpreterException{
		if(i>0)	return this.getB(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetB() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetB(i);
	}
	
	public double getOffsetC(int i) throws InterpreterException{
		if(i>0)	return this.getC(workOffsetsPos_ + (i - 1)*shift_);
		else return 0.0;
	}
	
	public double getWorkOffsetC() throws InterpreterException{
		int i = getCurrentWorkOffsetNum();
		return getOffsetC(i);
	}
	
	public double getRadius(int toolNum){
		// TODO real code needed
		return 0.0;
	}
	
	public boolean IsConstant(int num) throws InterpreterException{
		if(num <= 0) throw new InterpreterException("Reference to non initialized variable");
		else
			if(num == 1) return true;
			else return false;
	}
	
	public CNCPoint getHomePointG28() throws InterpreterException {
		CNCPoint homeCNCPoint =  new CNCPoint(this.getX(G28HomePos_), this.getY(G28HomePos_));
		return homeCNCPoint;
	}
	
	public CNCPoint getHomePointG30() throws InterpreterException {
		CNCPoint homeCNCPoint =  new CNCPoint(this.getX(G30HomePos_), this.getY(G30HomePos_));
		return homeCNCPoint;
	}
	
}
