/*
 * @author Alexey Chernysh
 */

package ru.android_cnc.acnc.HAL.InvertorController;

public class InvertorController implements Runnable {
	
	private boolean ready = false;

	@Override
	public void run() {
		boolean doRun = true;
		while (doRun){
		}
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public boolean isReady() {
		return ready;
	}

}
