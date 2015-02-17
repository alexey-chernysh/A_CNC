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

package ru.android_cnc.acnc.Interpreter.Expression;

import Interpreter.Expression.Tokens.TokenAlgebra;
import Interpreter.InterpreterException;

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
	public double evalute() throws InterpreterException {
		double x = arg1.evalute();
		if(arg2 != null){
			double y = this.arg2.evalute();
			return this.fun_.evalute(x,y);
		} else return this.fun_.evalute(x);
	}
	
}
