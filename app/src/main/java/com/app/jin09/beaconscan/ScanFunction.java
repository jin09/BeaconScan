package com.app.jin09.beaconscan;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Harmeet on 20-06-2016.
 */
public class ScanFunction {

    private static final int SCAN_PERIOD = 2000;
    private static ArrayList<byte[]> byteList;
    private static Handler handler;
    @TargetApi(21)
    private static ScanCallback getLollipopCallback() {
        ScanCallback callback = new ScanCallback() {
            @Override
            public void onScanFailed(int errorCode) {
                Log.e("Lollipop Callback", "Error Code : " + errorCode);
            }

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                byte[] beacBytes = result.getScanRecord().getBytes();
                Log.d("bytespdu lollipop",Utilities.bytesToHex(beacBytes, true));
                Utilities.bytesToBeacon(beacBytes,result.getRssi());
                //Log.d("rssi",result.getRssi()+"");
            }
        };
        return callback;
    }

    @TargetApi(18)
    private static BluetoothAdapter.LeScanCallback getJellybeanCallback(){
        BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.d("bytespdu jellybean",Utilities.bytesToHex(scanRecord, true));
                Utilities.bytesToBeacon(scanRecord,rssi);
            }
        };
        return callback;
    }

    @TargetApi(18)
    private static void startJellyBeanScanner(final BluetoothAdapter bluetoothAdapter,final Context context){
        final BluetoothAdapter.LeScanCallback callback = getJellybeanCallback();
        handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.stopLeScan(callback);
                Utilities.sortBeaconsByRssi();
                Utilities.showOfferWithLowestRssi(context);
            }
        },SCAN_PERIOD);
        bluetoothAdapter.startLeScan(callback);
    }

    @TargetApi(21)
    private static void startLollipopScanner(BluetoothAdapter bluetoothAdapter, final Context context){
        final BluetoothLeScanner leScanner = bluetoothAdapter.getBluetoothLeScanner();
        final ScanCallback callback = getLollipopCallback();
        ScanSettings scanSettings = (new ScanSettings.Builder()).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        List<ScanFilter> scanFilters=null;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                leScanner.stopScan(callback);
                Utilities.sortBeaconsByRssi();
                Utilities.showOfferWithLowestRssi(context);
            }
        },SCAN_PERIOD);
        leScanner.startScan(scanFilters,scanSettings,callback);
    }

    public static void startLeScan(BluetoothAdapter bluetoothAdapter, Context context){
        //byteList.clear();
        if(Build.VERSION.SDK_INT<21){
            startJellyBeanScanner(bluetoothAdapter,context);
        }
        else if (Build.VERSION.SDK_INT>=21){
            startLollipopScanner(bluetoothAdapter,context);
        }
    }
}