package ru.android_cnc.acnc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CNCService extends Service {
    public CNCService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
