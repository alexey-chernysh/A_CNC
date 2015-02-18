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

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenAlgebra;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

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
	public double evalute() throws InterpreterException {
		double x1 = this.arg1.evalute();
		double x2 = this.arg2.evalute();
		return oper_.evalute(x1, x2);
	}
	
}
