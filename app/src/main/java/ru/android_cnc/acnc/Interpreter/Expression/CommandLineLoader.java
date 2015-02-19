/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import ru.android_cnc.acnc.Interpreter.Expression.Tokens.Token;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenAlfa;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenAlgebra;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenCommand;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenComment;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenDefaultFields;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenGroup;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenList;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenParameter;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenSequence;
import ru.android_cnc.acnc.Interpreter.Expression.Tokens.TokenValue;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.ExpressionVarAssignment;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.ExpressionVariable;
import ru.android_cnc.acnc.Interpreter.Expression.Variables.VarAssignmentList;
import ru.android_cnc.acnc.Interpreter.InterpreterException;

public class CommandLineLoader extends TokenSequence {

	protected ParamExpresionList wordList_ = new ParamExpresionList();
	protected CommandPairList commandSet_ = new CommandPairList();
	protected VarAssignmentList varAssignmentSet_ = new VarAssignmentList();
	private String messageString_ = null;

	public CommandLineLoader(String frameString) throws InterpreterException {
		super(frameString);
//		this.tokenList.printAllTokens();

		// split tokens in hardware's command sequence
		int index = this.tokenList.getNextIndex();
		while (index < this.tokenList.size()) {
			Token t = this.tokenList.get(index).setParsed();
			if(t instanceof TokenAlfa){
				ExpressionGeneral numExp = getItem(this.tokenList);
				TokenDefaultFields currentWord = ((TokenAlfa)t).getType();
				switch( currentWord.getGroup() ){
					case COMMAND:
						this.commandSet_.addCommand(new CommandPair( (TokenCommand)currentWord, numExp));
						break;
					case PARAMETER:
						this.wordList_.addWord((TokenParameter)currentWord, numExp);
						break;
					case MISC:
						switch( (TokenAlgebra)currentWord ){
						case VAR:
							t = this.tokenList.get(this.tokenList.getNextIndex()).setParsed();
							if(((TokenAlfa)t).getType() == TokenAlgebra.ASSIGN){
								ExpressionGeneral varValueExp = getItem(this.tokenList);
								ExpressionVarAssignment newVarAssign = new ExpressionVarAssignment(numExp, varValueExp);
								this.varAssignmentSet_.add(newVarAssign);
							} else throw new InterpreterException("Asigment symbol '=' needed", t.getStart());
							break;
						default:
						}
						break;
					default:
						throw new InterpreterException("Bad command", t.getStart());
				};
			}
			else {
				throw new InterpreterException("Bad command", t.getStart());
			}
			index = this.tokenList.getNextIndex();
		};

		// check block for comments
		for(int i=0; i<this.tokenList.size();i++){
			Token tkn = this.tokenList.get(i);
			if( tkn instanceof TokenComment)
				this.messageString_ = ((TokenComment)tkn).getMessage();
		}
	}
	
	private ExpressionGeneral getExpression(TokenList list) throws InterpreterException {
		// left bracket occurred, so parse token list for Expression until right bracket
		ExpressionGeneral item1 = getItem(list);
		TokenDefaultFields Operator1 = ((TokenAlfa)list.get(list.getNextIndex()).setParsed()).getType();
		if(Operator1.getGroup() == TokenGroup.ALGEBRA){
			ExpressionGeneral item2 = getItem(list);
			return getSubExpression(list, Operator1, item1, item2);
		} else {
			if(Operator1 == TokenAlgebra.RIGHT_BRACKET){
				return item1;
			} else throw new InterpreterException("Binary operator needed", 0);
		}
	}

	private ExpressionGeneral getSubExpression(TokenList list,
											   TokenDefaultFields Operator1, 
									   	       ExpressionGeneral item1, 
									   	       ExpressionGeneral item2) throws InterpreterException {
		TokenDefaultFields Operator2 = ((TokenAlfa)list.get(list.getNextIndex()).setParsed()).getType();
		if(Operator2.getGroup() == TokenGroup.ALGEBRA){
			ExpressionGeneral item3 = getItem(list);
			if(Operator1.getPrecedence() <= Operator2.getPrecedence()){
				return getSubExpression(list, Operator2, new ExpressionFunction((TokenAlgebra)Operator1, item1, item2), item3);
			} else {
				return new ExpressionFunction((TokenAlgebra)Operator1, item1, getSubExpression(list, Operator2, item2, item3));
			}
		} else {
			if(Operator2 == TokenAlgebra.RIGHT_BRACKET){
				return new ExpressionFunction((TokenAlgebra)Operator1, item1, item2);
			} else throw new InterpreterException("Binary operator needed", 0);
		}
	}
	
	private ExpressionGeneral getItem(TokenList list) throws InterpreterException {
		// parse token list for expressions and return next unparsed token index
		ExpressionGeneral result = null;
		int index = list.getNextIndex();
		if(index < list.size()){
			Token t = list.get(index);
			t.setParsed();
			if(t instanceof TokenValue){
				// if const is last item in expression, then return it as result
				result = new ExpressionValue(((TokenValue)t).getValue());
				return result;
			} else {
				switch(((TokenAlfa)t).getType().getGroup()){
					case FUNCTION:
						TokenDefaultFields funType = ((TokenAlfa)t).getType();
						index = list.getNextIndex();
						if(index < list.size()){
							t = list.get(index);
							t.setParsed();
							ExpressionGeneral funArg1 = null;
							if((t instanceof TokenAlfa)&&(((TokenAlfa)t).getType() == TokenAlgebra.LEFT_BRACKET)){
								funArg1 = this.getExpression(list);
							} else throw new InterpreterException("Left bracket '[' requred", t.getStart());
							if(funType != TokenAlgebra.ATAN){
								result = new ExpressionFunction((TokenAlgebra)funType, funArg1);
								return result;
							}else{
								index = list.getNextIndex();
								if(index < list.size()){
									t = list.get(index);
									t.setParsed();
									if((t instanceof TokenAlfa)&&(((TokenAlfa)t).getType() == TokenAlgebra.DIVIDE)){
										index = list.getNextIndex();
										if(index < list.size()){
											t = list.get(index);
											t.setParsed();
											if((t instanceof TokenAlfa)&&(((TokenAlfa)t).getType() == TokenAlgebra.LEFT_BRACKET)){
												ExpressionGeneral funArg2 = this.getExpression(list);
												result = new ExpressionFunction((TokenAlgebra)funType, funArg1, funArg2);
												return result;
											} else throw new InterpreterException("Left bracket '[' requred", t.getStart());
										}else throw new InterpreterException("Unxpected end of frame string", t.getStart());
									} else throw new InterpreterException("Slash symnol '/' requred", t.getStart());
								}else throw new InterpreterException("Unxpected end of frame string", t.getStart());
							}
						} else throw new InterpreterException("Unxpected end of frame string", t.getStart());
				case ALGEBRA: // unary sign
						switch((TokenAlgebra)(((TokenAlfa)t).getType())){ // unary operations + -
							case MINUS: // sign -, substitute as expression (0 - x)
								ExpressionGeneral nextItem = this.getItem(list);
								ExpressionValue  zero = new ExpressionValue(0.0);
								result = (ExpressionGeneral) new ExpressionAlgebra(TokenAlgebra.MINUS, zero, nextItem);
								return result;
							case PLUS: // sign +, nothing to do
								result = this.getItem(list);
								return result;
							default:
								throw new InterpreterException("Unsupported unary operation", t.getStart());
						}
				case MISC: 
						switch((TokenAlgebra)(((TokenAlfa)t).getType())){ 
							case VAR: 
								ExpressionGeneral varNum = this.getItem(list);
								result = (ExpressionGeneral) new ExpressionVariable(varNum);
								return result;
							case LEFT_BRACKET: 
								result = this.getExpression(list);
								return result;
							default:
								throw new InterpreterException("Unsupported var operation", t.getStart());
						}
				default:
//						this.tokenList.printAllTokens();
						throw new InterpreterException("Illegal expression syntax", t.getStart());
				}
			}
		} else throw new InterpreterException("Unxpected end of block string", 0);
	}

	@Override
	public String toString(){

		String result = this.commandSet_.toString() 
					  + this.wordList_.toString() 
					  + this.varAssignmentSet_.toString();
		if(messageString_ != null) result += "MESSAGE:" + messageString_;
		return result;
	}

	public String getMessageString() {
		return messageString_;
	}

}