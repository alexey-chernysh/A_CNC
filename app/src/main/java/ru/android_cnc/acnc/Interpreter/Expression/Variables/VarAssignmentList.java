/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Variables;

import java.util.ArrayList;

public class VarAssignmentList extends ArrayList<ExpressionVarAssignment> {

	@Override
	public String toString(){
		String result = "";
		
		for(int i=0; i<this.size(); i++){
			ExpressionVarAssignment currentVar = this.get(i);
			result += " " + currentVar.toString();
		}
		
		return result;
	}

}
