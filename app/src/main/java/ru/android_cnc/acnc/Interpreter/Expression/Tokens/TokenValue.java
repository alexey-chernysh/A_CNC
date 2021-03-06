/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import android.graphics.Color;

public class TokenValue extends Token {
	
	private double value_ = 0;
	
	public 	TokenValue(String str, double v, int s, int e){
		super(str, s, e, Color.BLACK, false);
		value_ = v;
	}
	
	public	double getValue(){ 
		return value_;
	}
}
