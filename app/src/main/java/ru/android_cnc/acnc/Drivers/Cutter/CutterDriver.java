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

package ru.android_cnc.acnc.Drivers.Cutter;

import android.os.Handler;
import android.view.View;

import ru.android_cnc.acnc.Drivers.CanonicalCommands.CanonCommandSequence;
import ru.android_cnc.acnc.Drivers.GeneralDriver;
import ru.android_cnc.acnc.Interpreter.Exceptions.InterpreterException;

public class CutterDriver implements GeneralDriver {
	
	private CanonCommandSequence commands_ = null;

	public CutterDriver(){
	}

	@Override
	public void load(CanonCommandSequence sourceCommands) throws InterpreterException {
		commands_ = sourceCommands;
	}

    Thread executionThread;
    Handler mHandler = new Handler();
    boolean paused = false;

	@Override
	public void start(View v) {
        final View view = v;
        if(executionThread == null){
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    view.postInvalidate();
                    mHandler.postDelayed(this, 100);
                }
            };
            mHandler.post(runnable);
            executionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int n = commands_.size();
                    for(int i=0; i<n; i++)commands_.get(i).execute();
                    mHandler.removeCallbacks(runnable);
                    view.postInvalidate();
                }
            });
        } else if(paused)this.resume();
        executionThread.start(); // запускаем
	}

	@Override
	public void pause() {
    }

	@Override
	public void resume() {
        if(!executionThread.isInterrupted()) {
            executionThread.interrupt();
            paused = false;
        }
	}

	@Override
	public void rewind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void forwind() {
		// TODO Auto-generated method stub
		
	}

}
