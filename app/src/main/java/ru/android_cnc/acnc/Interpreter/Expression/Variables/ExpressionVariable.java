/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import ru.android_cnc.acnc.Interpreter.Expression.ExpressionGeneral;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ExpressionVariable extends ExpressionGeneral {

	private ExpressionGeneral varNumExp_;
	
	public ExpressionVariable(ExpressionGeneral e) {
		this.varNumExp_ = e;
	}

	@Override
	public double evalute() throws InterpreterException {
		int varNum = this.varNumExp_.integerEvalute();
		return InterpreterState.vars_.get(varNum);
	}
	
}