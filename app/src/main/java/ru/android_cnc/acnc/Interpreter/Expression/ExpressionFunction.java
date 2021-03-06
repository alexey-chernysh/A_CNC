/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenAlgebra;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class ExpressionFunction extends ExpressionGeneral {

	private TokenAlgebra fun_ = null;
	private ExpressionGeneral arg1 = null;
	private ExpressionGeneral arg2 = null;

	public ExpressionFunction(TokenAlgebra f, ExpressionGeneral a) {
		this.fun_ = f;
		this.arg1 = a;
		this.setConstant(this.arg1.isConstant());
	}

	public ExpressionFunction(TokenAlgebra f, ExpressionGeneral a1, ExpressionGeneral a2) {
		this.fun_ = f;
		this.arg1 = a1;
		this.arg2 = a2;
		if(this.arg1.isConstant() && this.arg2.isConstant()) this.setConstant(true);
	}

	public ExpressionFunction(TokenAlgebra f) {
		this.fun_ = f;
	}

	@Override
	public double evaluate() throws EvolutionException {
		double x = arg1.evaluate();
		if(arg2 != null){
			double y = this.arg2.evaluate();
			return this.fun_.evaluate(x,y);
		} else return this.fun_.evaluate(x);
	}
	
}
