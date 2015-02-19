/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

public class InterpreterException extends Exception {

	private	String message_ = null;
	private int position_ = -1;

	public InterpreterException(){}
	
	public InterpreterException(String msg){
		message_ = msg;
	}

	public InterpreterException(String msg, int p){
		message_ = msg;
		position_ = p;
	}

	public String getMessage(){return message_;}

	public int getPosition(){ return position_; }
	
}
