/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import android.graphics.Color;

public class TokenUnlexedText extends Token {
	
	public	TokenUnlexedText(String str, int s, int e){
        super(str, s, e, Color.RED, false);
    }
	
}

