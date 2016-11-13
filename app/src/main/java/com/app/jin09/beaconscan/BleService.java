package com.app.jin09.beaconscan;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Harmeet on 19-06-2016.
 */
public class BleService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
