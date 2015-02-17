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

public enum TokenParameter implements TokenDefaultFields {

	A("A"), // A-axis of machine
	
	B("B"), // B-axis of machine
	
	C("C"), // C-axis of machine
	
	D("D"), // tool number in radius compensation 
	
	H("H"), // tool length offset index
	
	I("I"), // X-axis offset for arcs, X offset in G87 canned cycle
	
	J("J"), // Y-axis offset for arcs, Y offset in G87 canned cycle
	
	K("K"), // Z-axis offset for arcs, Z offset in G87 canned cycle
	
	L("L"), // number of repetitions in canned cycles
	
	P("P"), // dwell time in canned cycles,	dwell time with G4,	key used with G10
	
	Q("Q"), // feed increment in G83 canned cycle
	
	R("R"), // arc radius, canned cycle plane
	
	U("U"), // Synonymous with A
	
	V("V"), // Synonymous with B
	
	W("W"), // Synonymous with C
	
	X("X"), // X-axis of machine
	
	Y("Y"), // Y-axis of machine
	
	Z("Z"); // Z-axis of machine

	private String alfa_;
	private TokenGroup group_ = TokenGroup.PARAMETER;
	private int precedence_ = -1;
	
	private TokenParameter(String a){
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
