/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Geometry.CNCPoint;

public class ParamExpressionList {
	
	private static final int size_ = TokenParameter.Z.ordinal() + 1;
	private ExpressionGeneral[] expressionList = new ExpressionGeneral[size_];
	
	public ParamExpressionList(){
		for(int i=0; i<size_; i++) {
			expressionList[i] = null;
		}
	};

	public void addWord(TokenParameter w, ExpressionGeneral e) throws InterpreterException {
		int n = w.ordinal();
		if(expressionList[n] == null) expressionList[n] = e;
		else throw new InterpreterException("Twice parameter " + w.toString() + ";");
	}

	public int getLength() {
		return size_;
	}

	public ExpressionGeneral get(int i){
		return expressionList[i];
	}

	@Override
	public String toString(){
		String result = "";
		
		for(int i=0; i< ParamExpressionList.size_; i++){
			ExpressionGeneral currentExp = this.expressionList[i];
			if(currentExp != null){
				try {
					result += " " + TokenParameter.values()[i].toString() + currentExp.evalute();
				} catch (InterpreterException e) {
					e.printStackTrace();
				}
			};
		}
		
		return result;
	}

	public boolean has(TokenParameter word){
		if(expressionList[word.ordinal()] != null) return true;
		else return false;
	}
	
	public boolean hasXYZ() {
		return (has(TokenParameter.X)||has(TokenParameter.Y)||has(TokenParameter.Z));
	}

    public boolean hasABC() {
        return (has(TokenParameter.A)||has(TokenParameter.B)||has(TokenParameter.C));
    }

	public double get(TokenParameter word) throws InterpreterException {
		int i = word.ordinal();
		if(expressionList[i] != null) return expressionList[i].evalute();
		else return 0.0;
	}

	public int getInt(TokenParameter word) throws InterpreterException {
		int i = word.ordinal();
		if(expressionList[i] != null) return expressionList[i].integerEvalute();
		else return 0;
	}
	
	public CNCPoint getPoint() throws InterpreterException{
		if(this.hasXYZ()) return new CNCPoint(this.get(TokenParameter.X), this.get(TokenParameter.Y));
		else return null;
	}

}
