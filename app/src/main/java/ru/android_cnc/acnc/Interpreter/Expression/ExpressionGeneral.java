/*
 * @author Alexey Chernysh
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

    @Override
    public String toString(){
        Double tmp = 0.0;
        try{
            tmp = this.evalute();
        }
        catch (InterpreterException ie){

        }
        return tmp.toString();
    }

}
