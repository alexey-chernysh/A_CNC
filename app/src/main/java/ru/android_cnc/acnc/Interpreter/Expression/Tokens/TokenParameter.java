/*
 * @author Alexey Chernysh
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

	TokenParameter(String a){
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
		return -1;
	}	

}
