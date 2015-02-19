/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import ru.android_cnc.acnc.Interpreter.InterpreterException;

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
	
	public double get(int num) throws InterpreterException {
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

