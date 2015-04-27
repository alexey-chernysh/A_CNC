/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

import ru.android_cnc.acnc.Interpreter.Expression.Variables.VariablesSet;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class ToolHeightCompensation {

    private boolean on = false;
    private double height = 0.0;
    private static ToolHeightCompensation instance = new ToolHeightCompensation();


    public ToolHeightCompensation(){}
    public static ToolHeightCompensation getInstance() {
        return instance;
    }

    public ToolHeightCompensation(double h){ this.height = h; }
    public void setHeight(int toolNum) throws InterpreterException {
        this.height = VariablesSet.getToolHeight(toolNum);
    }
    public void setOn(){this.on = true;}
    public void setOff(){ this.on = false; }
    public boolean isOn(){ return this.on; }
    public double getHeight(){ return this.height; }
}
