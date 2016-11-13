package com.app.jin09.beaconscan;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    Handler mHandler;
    private GoogleApiClient mGoogleApiClient;
    ArrayList<byte[]> bList;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        bList = new ArrayList<byte[]>();
        mHandler = new Handler();
        // Initializes Bluetooth adapter.
        Log.d(TAG, "onCreate........");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,12);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
        /*
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        ScanFilter sf;
        ScanFilter.Builder sfBuilder = new ScanFilter.Builder();
        sfBuilder.setServiceUuid(ParcelUuid.fromString("0000feaa-0000-1000-8000-00805f9b34fb"), ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"));
        sf =  sfBuilder.build();
        filters.add(sf);
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause!!");
//        mScanner.stopScan(mScanCallback);
        /*Intent i = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,1,i,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000,pi);*/
    }

    public void scanBle(View view) {
        //ScanFunction sf = new ScanFunction();
        //sf.startLeScan(mBluetoothAdapter);
        ScanFunction.startLeScan(mBluetoothAdapter,this);
        //scanLeDevice(true);
        //mScanner.startScan(filters,settings,mScanCallback);
        //mBluetoothAdapter.startLeScan(mLeScanCallBack);
    }

    public void stopBle(View view) {
        //mScanner.stopScan(mScanCallback);
        //mBluetoothAdapter.stopLeScan(mLeScanCallBack);
        Log.d("beaconSize",App.list.size()+"");
        Log.d("original",App.tList.toString());
    }

    public void arraySize(View view){
        Intent i = new Intent(this,BackgroundBleScanner.class);
        startService(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop!!");
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected........");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET
                }, 10);
            }
        } else {
            turnOnLocationSetting(this,mGoogleApiClient);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    turnOnLocationSetting(this,mGoogleApiClient);
                    finish();
                    System.exit(0);
                }
                return;
        }
    }

    public void turnOnLocationSetting(final Activity activity, GoogleApiClient mGoogleApiClient) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Settings change unavailable. We have no way to fix the settings so we won't show the dialog.");
                        break;
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "connection to google play suspended!!! ..");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection to google play failed ..");
    }
}
