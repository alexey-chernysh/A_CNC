/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.Interpreter.State;

public class ToolSet {
	
	private int currentTool_;
	private final static int maxTool_ = 255;
	private double[] toolRadius = new double[maxTool_];
	private double[] toolHeight = new double[maxTool_];
	
	public ToolSet(){
		currentTool_ = 0;
		for(int i=0; i<maxTool_; i++){
			toolRadius[i] = 0.0;
			toolHeight[i] = 0.0;
		}
	}
	
	public int getCurrentTool() {
		return currentTool_;
	}

	public void setCurrentTool(int num) {
		this.currentTool_ = num;
	}
	
	public double getMaxToolNum() {
		return maxTool_;
	}

	public double getToolRadius(int toolNum) {
		return toolRadius[toolNum];
	}
	
	public void setToolRadius(int toolNum, double R){
		this.toolRadius[toolNum] = R;
	}

	public double getToolHeight(int toolNum) {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	public void setToolHeight(int toolNum, double H){
		this.toolHeight[toolNum] = H;
	}

}
