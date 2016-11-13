package com.app.jin09.beaconscan;

/**
 * Created by gautam on 13-11-2016.
 */

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackgroundBleScanner extends Service {

    BluetoothAdapter mBluetoothAdapter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            ScanFunction.startLeScan(mBluetoothAdapter,this);
            //throwNotifications.pushNotification(this,"Nike","Nike","ibsjk nike adi kjfn");
        }
        Intent i = new Intent(this,BackgroundBleScanner.class);
        PendingIntent pi = PendingIntent.getService(this,12,i,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 9000,pi);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ondestroy","Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

