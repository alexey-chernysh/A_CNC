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
		return this.type_.getAlfa() + "=" + this.commandExpression_.toString();
	}
	
}
