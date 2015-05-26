/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class Token {

	private int start_ = -1;
	private int end_ = -1;
	private String source_;
    protected final int iColor_;
    protected final boolean bBold;

    private boolean parsed_ = false;

	public 	Token(String s, int columnStart, int columnEnd, int sColor, boolean b){
		source_ = s;
		start_ = columnStart;
		end_ = columnEnd;
        iColor_ = sColor;
        bBold = b;
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
		return " " + this.getClass().toString() + " = " + getSubString()
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

    public void setColorSpan(Spannable s, int pos){
        s.setSpan(new ForegroundColorSpan(this.iColor_),
                pos + this.getStart(),
                pos + this.getEnd() + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(bBold)
            s.setSpan(new StyleSpan(Typeface.BOLD),
                    pos + this.getStart(),
                    pos + this.getEnd() + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

}