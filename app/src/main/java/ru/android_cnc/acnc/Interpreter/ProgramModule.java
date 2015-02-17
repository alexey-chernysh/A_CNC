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

package ru.android_cnc.acnc.Interpreter;

import java.util.ArrayList;

public class ProgramModule {
	
	private int startLine_ = -1;
	private int endLine_ = -1;
	public final int num;
	private ArrayList<LineLoader> programBody_;
	
	public ProgramModule(int n, ArrayList<LineLoader> lineArray){
		this.num = n;
		programBody_ = lineArray;
	}
	
	public void setStart(int sl){
		this.startLine_ = sl;
	}

	public void setEnd(int el){
		this.endLine_ = el;
	}
	
	public void evalute() throws InterpreterException{
		if((this.startLine_ >= 0) && (this.endLine_ >= startLine_)){
			for(int i=this.startLine_; i<=this.endLine_; i++)
				programBody_.get(i).evalute();
		} else throw new InterpreterException("Call of not initialized module!");
	}

}
