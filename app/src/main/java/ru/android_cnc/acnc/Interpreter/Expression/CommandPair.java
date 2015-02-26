/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenCommand;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

public class CommandPair { // pair of command indrntifier alfa and associated expression
	
	private TokenCommand type_;
	private ExpressionGeneral commandExpression_;

	public CommandPair(TokenCommand t, ExpressionGeneral exp){
		this.type_ = t;
		this.commandExpression_ = exp;
	}
	
	public double getCurrentValue() throws InterpreterException {
        return this.commandExpression_.evalute();
	}

	public ExpressionGeneral getValueExpression() {
		return this.commandExpression_;
	}
	
	public TokenCommand getType(){
		return this.type_;
	}
	
	@Override
	public String toString(){
		return this.getType().toString() + this.commandExpression_.toString();
	}
	
}
