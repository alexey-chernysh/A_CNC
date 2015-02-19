/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public class TokenValue extends Token {
	
	private double value_ = 0;
	
	public 	TokenValue(String str, double v, int s, int e){
		super(str, s, e);  
		value_ = v;
	}
	
	public	double getValue(){ 
		return value_;
	}
	
	public	TokenValue setValue(double v){ 
		value_ = v; 
		return this;
	}
}
