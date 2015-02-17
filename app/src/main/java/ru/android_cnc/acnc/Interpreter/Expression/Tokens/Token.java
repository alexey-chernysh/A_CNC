/*
 * Copyright 2014-2015 Alexey Chernysh
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ru.android_cnc.acnc.Interpreter.Expression.Tokens;

public class Token { 

	private int start_ = -1;
	private int end_ = -1;
	private boolean parsed_ = false;
	private String source_;
	
	
	public 	Token(String s, int columnStart, int columnEnd){
		source_ = s;
		start_ = columnStart;
		end_ = columnEnd;
	}

	public 	int getStart(){ return start_;	}
	
	public 	Token setStart(int s){
		start_ = s; 
		return this;
	}

	public 	int getEnd(){ return end_;	}
	
	public 	Token setEnd(int e){
		end_ = e; 
		return this;
	}
	
	public	int getLength(){ return end_ - start_ + 1; }
	
	public	void printLine(){
		System.out.println(this.toString());
	}
	
	@Override
	public String toString(){
		String result = "";
		int i;
		if(source_.length()>0){
			for( i = 0; i < start_; i++ )
				result += "_";
			for( i = start_; i <= end_; i++ )
				result += source_.charAt(i);
		};
		return result;
	}

	public	String getSubString(){
		return source_.substring(this.start_, this.end_+1);
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
}