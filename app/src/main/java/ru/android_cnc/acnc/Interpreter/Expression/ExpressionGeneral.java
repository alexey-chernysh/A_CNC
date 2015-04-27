/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Exceptions.EvolutionException;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class ExpressionGeneral { // general expression used in NGC274 code
	
	private boolean constant = false;

	public double evaluate() throws EvolutionException {
		new EvolutionException("Empty expression evolution!");
		return 0.0;
	}
	
	public int integerEvaluate() throws EvolutionException {
		double resultDouble  = this.evaluate();
		int    resultInteger = (int)resultDouble;
		if(resultDouble != ((double)resultInteger)) 
			throw new EvolutionException("Integer value required!");
		return resultInteger; 
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

    @Override
    public String toString(){
        Double tmp = 0.0;
        try{
            tmp = this.evaluate();
        }
        catch (EvolutionException ie){

        }
        return tmp.toString();
    }

}
