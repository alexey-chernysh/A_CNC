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
