/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenCommand;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class CommandPair { // pair of command indrntifier alfa and associated expression
	
	private TokenCommand type_;
    private int tokenPos_;
	private ExpressionGeneral commandExpression_;

	public CommandPair(TokenCommand t, ExpressionGeneral exp, int pos){
		this.type_ = t;
		this.commandExpression_ = exp;
        this.tokenPos_ = pos;
	}
	
	public double getCurrentValue() throws EvolutionException {
        return this.commandExpression_.evaluate();
	}

	public ExpressionGeneral getValueExpression() {
		return this.commandExpression_;
	}
	
	public TokenCommand getType(){
		return this.type_;
	}
	
	@Override
	public String toString(){
		return this. getType().toString() + this.commandExpression_.toString();
	}

    public int getPosInString() {
        return this.tokenPos_;
    }
}
