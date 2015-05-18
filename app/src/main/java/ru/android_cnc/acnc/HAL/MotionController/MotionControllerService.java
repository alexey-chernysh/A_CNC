package ru.android_cnc.acnc.HAL.MotionController;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import ru.android_cnc.acnc.Geometry.CNCPoint;
import ru.android_cnc.acnc.R;

public class MotionControllerService extends Service {

    private final static String LOG_TAG = "MC service";

    private CNCPoint currentPosition = null;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "Destroyed!");

        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS), 0);
        settings.edit().putFloat(getString(R.string.PREF_LAST_POSITION_X), (float)currentPosition.getX()).commit();
        settings.edit().putFloat(getString(R.string.PREF_LAST_POSITION_Y), (float) currentPosition.getY()).commit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public CNCPoint getCurrentPosition() {
        return currentPosition;
    }

}
