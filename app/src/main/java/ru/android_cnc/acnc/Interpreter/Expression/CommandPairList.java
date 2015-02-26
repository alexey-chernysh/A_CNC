/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.Expression;

import java.util.ArrayList;

public class CommandPairList extends ArrayList<CommandPair> {
	
	public void addCommand(CommandPair e){
		this.add(e);
	}

	@Override
	public String toString(){
        if(this.size()>0){
            String result = "Commands:";
            for(int i=0; i<this.size(); i++){
                CommandPair currentPair = this.get(i);
                result += currentPair.toString() + " ";
            };
            return result;
        } else {
            return  null;
        }
	}

}
