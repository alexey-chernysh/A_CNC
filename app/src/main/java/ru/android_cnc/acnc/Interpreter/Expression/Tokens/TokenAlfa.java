/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public class TokenAlfa extends Token {
	
	private TokenDefaultFields type_;
	
	public	TokenAlfa(String st, TokenDefaultFields t, int s, int e){
		super(st, s, e); 
		this.type_ = t;
	}
	
	public	TokenDefaultFields getType(){ return this.type_; }

}
