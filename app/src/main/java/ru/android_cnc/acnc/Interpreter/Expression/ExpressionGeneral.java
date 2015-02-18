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

import ru.android_cnc.acnc.Interpreter.InterpreterException;

public class ExpressionGeneral { // general expression used in NGC274 code
	
	private boolean constant = false;

	public double evalute() throws InterpreterException {
		new InterpreterException("Empty expression evolution!");
		return 0.0;
	}
	
	public int integerEvalute() throws InterpreterException { 
		double resultDouble  = this.evalute();
		int    resultInteger = (int)resultDouble;
		if(resultDouble != ((double)resultInteger)) 
			throw new InterpreterException("Integer value required!");
		return resultInteger; 
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

}
