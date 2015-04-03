/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

public class ExpressionValue extends ExpressionGeneral {
	
	private double value_;
	
	public ExpressionValue(double v){
		value_ = v;
		this.setConstant(true);
	}

	@Override
	public double evaluate(){
		return value_;
	}
	
}
