/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import android.graphics.Color;

public class TokenSeparator extends Token {
	
	public	TokenSeparator(String str, int s, int e){
        super(str, s, e, Color.BLACK);
    }
	
}

