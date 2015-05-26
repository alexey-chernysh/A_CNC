/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import android.text.Spannable;

import java.util.Iterator;
import java.util.LinkedList;

import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class TokenList extends LinkedList<Token> {
	
	private String sourceLine_;
	private String sourceLineUpperCase_;
	
	public
	TokenList(String frameLine) throws InterpreterException {
		super();
		
		this.sourceLine_ = frameLine;
		this.sourceLineUpperCase_ = this.sourceLine_.toUpperCase();
		
		if(this.sourceLine_.length() > 0) this.addFirst(new TokenUnlexedText(this.sourceLineUpperCase_, 0, this.sourceLine_.length()-1));
		else this.addFirst(new TokenUnlexedText(this.sourceLineUpperCase_, 0, 0));
	}
	
	protected 
	int addNewToken(Token newToken, int index){
		TokenUnlexedText newHead, newTail;
		Token replaced = this.get(index);
		this.remove(index);
		if(newToken.getStart()>replaced.getStart()){
			newHead = new TokenUnlexedText(this.sourceLineUpperCase_, replaced.getStart(), newToken.getStart()-1);
			this.add(index, newHead);
			index++;
		}
		this.add(index,newToken);
		if(newToken.getEnd()<replaced.getEnd()){
			newTail = new TokenUnlexedText(this.sourceLineUpperCase_, newToken.getEnd()+1, replaced.getEnd());
			this.add(index+1, newTail);
		}
		return index;
	}
	
	protected int getNextToken(int index){
		for(int i=index; i<this.size(); i++){
			Token t = this.get(i);
			if(!(t instanceof TokenComment) ){
				if(!(t instanceof TokenSeparator) ){
					return i;
				}
			}
		}
		return -1;
	}
	
	public int getNextIndex(){
		for(int i=0; i<this.size(); i++){
			Token tmp = this.get(i);
			if(tmp.isSignificant()&&(!tmp.isParsed()))
				return i;
		}
		return this.size();
	}
	
	protected int getNextInt(int index) throws InterpreterException {
		int i = index+1;
		while(i < this.size()){
			Token t = this.get(i);
			if((t instanceof TokenComment) || (t instanceof TokenSeparator)){
				i++;
			} else
				if(t instanceof TokenValue){
					double tmp = ((TokenValue)t).getValue();
					int result = (int)tmp;
					if(Math.abs(tmp - result) == 0.0){
						return result;
					} else {
						throw new InterpreterException("Double instead of integer. Integer requred", t.getStart());
					}
				} else {
					throw new InterpreterException("Integer value ommited", t.getStart());
				}
		}
		return -1;
	}
	
	public String getSourceLine(){
		return this.sourceLine_;
	}
	
	public String getSourceLineUpperCase(){
		return this.sourceLineUpperCase_;
	}

    @Override
	public String toString(){
		Iterator<Token> itr =  this.iterator();
        String result = "";
		while(itr.hasNext())
            result += itr.next().toString();
        return result;
	}

    public void setAllSpan(Spannable s, int pos){
		for (Token token : this) token.setColorSpan(s, pos);
    }

}
