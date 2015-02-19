/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public class Token { 

	private int start_ = -1;
	private int end_ = -1;
	private String source_;
    protected final String TOKEN_NAME = "Unlexed token";

    private boolean unlexed_ = true;
    private boolean parsed_ = false;

	public 	Token(String s, int columnStart, int columnEnd){
		source_ = s;
		start_ = columnStart;
		end_ = columnEnd;
	}

	public 	int getStart(){
        assert(start_ >= 0);
        return start_;
    }

	public 	int getEnd(){
        assert(end_ >= 0);
        return end_;
    }

	public	int getLength(){ return (end_ - start_ + 1); }

	@Override
	public String toString(){
		return " " + TOKEN_NAME + " = " + getSubString()
                + " at positions from " + this.start_
                + " to " + this.end_;
	}

	public	String getSubString(){
		return source_.substring(this.getStart(), this.getEnd()+1);
	}
	
	public boolean isSignificant(){
		if( this instanceof TokenComment ) return false;
		if( this instanceof TokenSeparator ) return false;
		if( this instanceof TokenUnlexedText ) return false;
		return true;
	}

	public boolean isParsed() {
		return parsed_;
	}

	public Token setParsed() {
		this.parsed_ = true;
		return this;
	}

    public boolean isUnlexed_() {
        return unlexed_;
    }

    public Token setLexed_() {
        this.unlexed_ = false;
        return this;
    }

}