/*
 * Copyright 2014-2015 Alexey Chernysh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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