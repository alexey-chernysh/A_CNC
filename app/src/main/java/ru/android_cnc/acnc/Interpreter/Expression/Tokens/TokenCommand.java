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

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public enum TokenCommand implements TokenDefaultFields {
	
	G("G"), // general command
	
	M("M"), // miscellaneous command
	
	N("N"), // line number
	
	O("O"), // program name
	
	S("S"), // spindle speed
	
	T("T"), // tool selection
	
	F("F"); // feed rate
	
	private String alfa_;
	private TokenGroup group_ = TokenGroup.COMMAND;
	private int precedence_ = -1;
	
	private TokenCommand(String a){
		alfa_ = a;
	}
	
	@Override
	public String getAlfa() {
		return alfa_;
	}

	@Override
	public TokenGroup getGroup() {
		return group_;
	}

	@Override
	public int getPrecedence() {
		return precedence_;
	}	

}
