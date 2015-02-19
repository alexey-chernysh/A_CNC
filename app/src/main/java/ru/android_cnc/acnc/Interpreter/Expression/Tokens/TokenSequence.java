/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import ru.android_cnc.acnc.Interpreter.InterpreterException;
import ru.android_cnc.acnc.Interpreter.State.InterpreterState;

public class TokenSequence {
	
	public TokenList tokenList = null;
		
	public TokenSequence(String frameString) throws InterpreterException {
		this.tokenList = new TokenList(frameString);

		if(frameString.length()>0){
			if(frameString.charAt(0) == '/') {
				if ( InterpreterState.IsBlockDelete){
					TokenComment comment = new TokenComment(this.tokenList.getSourceLine(), TokenComment.CommentKeyWord.BLOCK_DELETE_SLASH, 0,frameString.length()-1,frameString);
					this.tokenList.addNewToken(comment, 0);
					return;
				} else {
					TokenComment comment = new TokenComment(this.tokenList.getSourceLine(), TokenComment.CommentKeyWord.BLOCK_DELETE_SLASH, 0, 0,frameString);
					this.tokenList.addNewToken(comment, 0);
				};
			};
			try {
				getAllComments(this.tokenList.getSourceLine());
				getAllSeparator(this.tokenList.getSourceLineUpperCase());
				getAllAlfaTokens(this.tokenList.getSourceLineUpperCase());
				getAllValues(this.tokenList.getSourceLineUpperCase());
			} catch (InterpreterException le) {
				System.out.println("Lexer exception: " + le.getMessage() + " at position "+ le.getPosition() + " => "+frameString);
			} 
		};
		return;
	}
	
	private
	void getAllAlfaTokens(String sourceString) {
		for(TokenAlgebra tokFun: TokenAlgebra.values()){
			String sample = tokFun.getAlfa();
			int curTokenNum = 0;
			while(curTokenNum < this.tokenList.size()){
				Token currentToken = this.tokenList.get(curTokenNum);
				if(currentToken instanceof TokenUnlexedText){
					int startPos = currentToken.getStart();
					String source = sourceString.substring(startPos, currentToken.getEnd()+1);
					int funPos = source.indexOf(sample);
					if(funPos >= 0){
						TokenAlfa newFun = new TokenAlfa(sourceString, tokFun, startPos+funPos,startPos+funPos+sample.length()-1);
						curTokenNum = this.tokenList.addNewToken(newFun, curTokenNum);
					};
				};
				curTokenNum++;
			}
		}
		for(TokenCommand tokComm: TokenCommand.values()){
			String sample = tokComm.getAlfa();
			int curTokenNum = 0;
			while(curTokenNum < this.tokenList.size()){
				Token currentToken = this.tokenList.get(curTokenNum);
				if(currentToken instanceof TokenUnlexedText){
					int startPos = currentToken.getStart();
					String source = sourceString.substring(startPos, currentToken.getEnd()+1);
					int funPos = source.indexOf(sample);
					if(funPos >= 0){
						TokenAlfa newFun = new TokenAlfa(sourceString, tokComm, startPos+funPos,startPos+funPos+sample.length()-1);
						curTokenNum = this.tokenList.addNewToken(newFun, curTokenNum);
					};
				};
				curTokenNum++;
			}
		}
		for(TokenParameter tokParam: TokenParameter.values()){
			String sample = tokParam.getAlfa();
			int curTokenNum = 0;
			while(curTokenNum < this.tokenList.size()){
				Token currentToken = this.tokenList.get(curTokenNum);
				if(currentToken instanceof TokenUnlexedText){
					int startPos = currentToken.getStart();
					String source = sourceString.substring(startPos, currentToken.getEnd()+1);
					int funPos = source.indexOf(sample);
					if(funPos >= 0){
						TokenAlfa newFun = new TokenAlfa(sourceString, tokParam, startPos+funPos,startPos+funPos+sample.length()-1);
						curTokenNum = this.tokenList.addNewToken(newFun, curTokenNum);
					};
				};
				curTokenNum++;
			}
		}
	}
	
	private 
	void getAllComments(String sourceString) throws InterpreterException{
		getAllCommentSemicolon(sourceString);
		getAllCommentParenthesis(sourceString);
	}
	
	private
	void getAllCommentSemicolon(String sourceString) {
		int len = sourceString.length(); 
		int commentStart = -1;
		TokenComment.CommentKeyWord newKey = TokenComment.CommentKeyWord.PARENTHESIS;
		for(TokenComment.CommentKeyWord k: TokenComment.commentAtFirstPos){
			int i = sourceString.indexOf(k.key);
			if(i>commentStart){
				commentStart = i;
				newKey = k;
			}
		};
		if( commentStart >= 0 ){
			TokenComment newComment = new TokenComment(sourceString, newKey, commentStart, len-1,sourceString);
			this.tokenList.addNewToken(newComment, 0);
		};
	}
	
	private
	void getAllCommentParenthesis( String sourceString) throws InterpreterException {
		int curTokenNum = 0;
		while(curTokenNum < this.tokenList.size()){
			Token currentToken = this.tokenList.get(curTokenNum);
			if(currentToken instanceof TokenUnlexedText){
				int commentStart = -1;
				int commentEnd = -1;
				for(int i = currentToken.getStart(); i <= currentToken.getEnd(); i++)
					if( sourceString.charAt(i) == '(') {
						commentStart = i;
						break;
					}
				if(commentStart>=0){
					for(int i = commentStart+1; i <= currentToken.getEnd(); i++)
						if( sourceString.charAt(i) == ')') {
							commentEnd = i;
							break;
						}
					if(commentEnd > commentStart){
						TokenComment newComment = new TokenComment(sourceString, TokenComment.CommentKeyWord.PARENTHESIS, commentStart, commentEnd,sourceString);
						curTokenNum = this.tokenList.addNewToken(newComment, curTokenNum);
					} else throw new InterpreterException("Unexpexted end of line! Symbol ) omitted",commentEnd);
				};
			}
			curTokenNum++;
		}
	}

	private
	void getAllSeparator(String sourceString) {
		int curTokenNum = 0;
		while(curTokenNum < this.tokenList.size()){
			Token currentToken = this.tokenList.get(curTokenNum);
			if(currentToken instanceof TokenUnlexedText){
				int separatorPos;
				if((separatorPos = getSeparatorPos(sourceString, currentToken.getStart(), currentToken.getEnd()))>=0){
					TokenSeparator newSeparator = new TokenSeparator(sourceString, separatorPos,separatorPos);
					curTokenNum = this.tokenList.addNewToken(newSeparator, curTokenNum);
				};
			};
			curTokenNum++;
		}
	}
	
	private
	int getSeparatorPos(String frame, int start, int end){
		int result = -1;
		for(int i = start; i <= end; i++){
			char currentChar = frame.charAt(i);
			if((currentChar == ' ') || (currentChar == '\t') || (currentChar == '\r')){
				return i;
			}
		}
		return result;
	}
	
	private
	void getAllValues(String sourceString) throws InterpreterException {
		int curTokenNum = 0;
		while(curTokenNum < this.tokenList.size()){
			Token currentToken = this.tokenList.get(curTokenNum);
			if(currentToken instanceof TokenUnlexedText){
				int startPos = currentToken.getStart();
				String source = sourceString.substring(startPos, currentToken.getEnd()+1);
				int len = source.length();
				try{
					double tmp = Double.parseDouble(source);
					TokenValue newValue = new TokenValue(sourceString, tmp, startPos, startPos+len-1);
					curTokenNum = this.tokenList.addNewToken(newValue, curTokenNum);
				}
				catch(NumberFormatException e){
					this.tokenList.printAllTokens();
					throw new InterpreterException("Illegal value or symbol", startPos);
				}
			};
			curTokenNum++;
		}
	}
}
