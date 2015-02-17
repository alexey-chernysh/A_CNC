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

import Interpreter.InterpreterException;

public 	class VarArray {
	
	private static final int arraySize_ = 10320;
	private double[] variables_ = new double[arraySize_];
	private int[] assigment_counter_ = new int[arraySize_];

	public VarArray(){
		for( int i = 0; i<arraySize_; i++){
			variables_[i] = 0.0;
			assigment_counter_[i] = 0;
		}
	}
	
	private int getAssignmentCounter(int num){
		return this.assigment_counter_[num];
	};
	
	public double get(int num) throws InterpreterException{
		if((num>=0)&(num<arraySize_)){ 
			if(getAssignmentCounter(num) > 0) return this.variables_[num];
			else throw new InterpreterException("Reference to non initialized variable");
		}
		else throw new InterpreterException("Illegal parameter number");
	}
	
	public void set(int num, double val) throws InterpreterException{
		if((num>=0)&(num<arraySize_)){ 
			this.variables_[num] = val;
			this.assigment_counter_[num]++;
		}
		else throw new InterpreterException("Illegal parameter number");
	}
	
}

