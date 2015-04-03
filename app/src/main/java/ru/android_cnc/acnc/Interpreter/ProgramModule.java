/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter;

import java.util.ArrayList;

public class ProgramModule {
	
	private int startLine_ = -1;
	private int endLine_ = -1;
    public final String moduleName; // program/subroutine number/name (o code)
	private ArrayList<LineLoader> programBody_;
	
	public ProgramModule(int n, ArrayList<LineLoader> lineArray){
        Integer num = n;
		this.moduleName = num.toString();
		programBody_ = lineArray;
	}

    public ProgramModule(String name, ArrayList<LineLoader> lineArray){
        this.moduleName = name;
        programBody_ = lineArray;
    }

    public void setStart(int sl){
		this.startLine_ = sl;
	}

	public void setEnd(int el){
		this.endLine_ = el;
	}
	
	public void evaluate() throws InterpreterException{
		if((this.startLine_ >= 0) && (this.endLine_ >= startLine_)){
			for(int i=this.startLine_; i<=this.endLine_; i++)
				programBody_.get(i).evaluate();
		} else throw new InterpreterException("Call of not initialized module!");
	}

}
