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

import java.util.EnumSet;

public class TokenComment extends Token {
	
	private CommentKeyWord key_;
	private String message_ = null;
	
	public 	TokenComment(String str, CommentKeyWord k, int s, int e, String source){ 
		super(str, s, e);
		this.key_ = k;
		String tmp = this.getCommentText(source);
		String msgString = "MSG";
		int msgPos = tmp.indexOf(msgString);
		if( msgPos >= 0 ) {
			this.message_ = tmp.substring(msgPos + msgString.length());
		};
	}

	public String getCommentText(String source) {
		switch(this.key_){
		case DOUBLE_SLASH:
		case SEMICOLON:
		case PERCENT:
		case BLOCK_DELETE_SLASH:
			return source.substring(this.getStart()+key_.key.length(), this.getEnd()+1);
		case PARENTHESIS:
		default:
			return source.substring(this.getStart()+1, this.getEnd());
		}
	}
	
	public String getMessage() {
		return message_;
	}

	public enum CommentKeyWord{
		DOUBLE_SLASH("//"),
		SEMICOLON(";"),
		PERCENT("%"),
		BLOCK_DELETE_SLASH("/"),
		PARENTHESIS("()");
		
		public String key;
		
		private CommentKeyWord(String k){
			this.key = k;
		}
	}
	
	public static EnumSet<CommentKeyWord> commentAtFirstPos = EnumSet.range(CommentKeyWord.DOUBLE_SLASH, CommentKeyWord.PERCENT);
}

