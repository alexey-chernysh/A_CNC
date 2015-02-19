/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import ru.android_cnc.acnc.Interpreter.Expression.ExpressionGeneral;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class ExpressionVarAssignment {

	private ExpressionGeneral varNumExp_;
	private ExpressionGeneral varValExp_;
	private int lastNum_ = 0;
	private double lastValue_ = 0.0;
	
	public ExpressionVarAssignment(ExpressionGeneral en, ExpressionGeneral ev) {
		this.varNumExp_ = en;
		this.varValExp_ = ev;
	}

	public void evalute() throws InterpreterException {
		this.lastNum_ = this.varNumExp_.integerEvalute();
		this.lastValue_ = this.varValExp_.evalute();
		InterpreterState.vars_.set(this.lastNum_, this.lastValue_);
	}

	@Override
	public String toString(){
		String result = "Var" + this.lastNum_ + " = " + this.lastValue_;
		return result;
	}

}