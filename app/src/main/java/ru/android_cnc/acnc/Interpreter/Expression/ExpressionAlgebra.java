/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenAlgebra;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class ExpressionAlgebra extends ExpressionGeneral {
	
	private TokenAlgebra oper_;
	private ExpressionGeneral arg1 = null;
	private ExpressionGeneral arg2 = null;

	public ExpressionAlgebra( TokenAlgebra o,
							  ExpressionGeneral a1, 
							  ExpressionGeneral a2) {
		this.oper_ = o; 
		this.arg1 = a1;
		this.arg2 = a2;
		if(this.arg1.isConstant() && this.arg2.isConstant()) this.setConstant(true);
	}

	@Override
	public double evaluate() throws EvolutionException {
		double x1 = this.arg1.evaluate();
		double x2 = this.arg2.evaluate();
		return oper_.evaluate(x1, x2);
	}
	
}
