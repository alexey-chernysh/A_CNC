package ru.android_cnc.acnc.HAL.MotionController;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;

import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.R;

public class MotionControllerService extends Service {

    private final static String LOG_TAG = "MC service";

    private CNCPoint currentPosition = null;

    private static double maxVelocity = 0.0;          // v        - mm/sec
    private static double maxFirstDerivative = 0.0;   // dv/dt    - mm/sec/sec
    private static double maxSecondDerivative = 0.0;  // dv/dt/dt - mm/sec/sec/sec
    private static double x_mm_in_step = 0.0;  // mm in step
    private static double y_mm_in_step = 0.0;  // mm in step
    private static double TikInMM = 1.0/x_mm_in_step/y_mm_in_step;

    public MotionControllerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Created!");

        //load last saved position as current
        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS), 0);
        double cp_X = settings.getFloat(getString(R.string.PREF_LAST_POSITION_X), 0.0f);
        double cp_Y = settings.getFloat(getString(R.string.PREF_LAST_POSITION_Y), 0.0f);
        currentPosition = new CNCPoint(cp_X,cp_Y);
        maxVelocity = settings.getFloat(getString(R.string.PREF_MAX_VELOCITY), 10000.0f/60.0f);
        maxFirstDerivative = settings.getFloat(getString(R.string.PREF_MAX_FIRST_DERIVATIVE), 40.0f);
        maxSecondDerivative = settings.getFloat(getString(R.string.PREF_MAX_SECOND_DERIVATIVE), 10.0f);
        x_mm_in_step = settings.getFloat(getString(R.string.PREF_MM_IN_STEP_X),  0.007048f);
        y_mm_in_step = settings.getFloat(getString(R.string.PREF_MM_IN_STEP_Y),  0.007048f);
    }

    Thread tikThread = null;
    boolean shouldContinue = true;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tikThread = new Thread() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                try {
                    while(shouldContinue) {
                        sleep(1000);
//                        handler.post(this);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        tikThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            shouldContinue = false;
            tikThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS), 0);
        settings.edit().putFloat(getString(R.string.PREF_LAST_POSITION_X), (float) currentPosition.getX()).commit();
        settings.edit().putFloat(getString(R.string.PREF_LAST_POSITION_Y), (float) currentPosition.getY()).commit();
        settings.edit().putFloat(getString(R.string.PREF_MAX_VELOCITY), (float) maxVelocity).commit();
        settings.edit().putFloat(getString(R.string.PREF_MAX_FIRST_DERIVATIVE), (float) maxFirstDerivative).commit();
        settings.edit().putFloat(getString(R.string.PREF_MAX_SECOND_DERIVATIVE), (float) maxSecondDerivative).commit();
        settings.edit().putFloat(getString(R.string.PREF_MM_IN_STEP_X), (float) x_mm_in_step).commit();
        settings.edit().putFloat(getString(R.string.PREF_MM_IN_STEP_Y), (float) y_mm_in_step).commit();

        Log.d(LOG_TAG, "Destroyed!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public CNCPoint getCurrentPosition() {
        return currentPosition;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public static double getX_mm_in_step() {
        return x_mm_in_step;
    }

    public static double getY_mm_in_step() {
        return y_mm_in_step;
    }

    public static double getTikInMM() {
        return TikInMM;
    }

}
