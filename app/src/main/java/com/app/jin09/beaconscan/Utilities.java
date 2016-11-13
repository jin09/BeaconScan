package com.app.jin09.beaconscan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Harmeet on 21-06-2016.
 */
public class Utilities {

    //Convert byte array to hexadecimal string
    public static String bytesToHex(byte[] in, boolean space) {
        final StringBuilder builder = new StringBuilder();
        if(space==true) {
            for (byte b : in)
                builder.append(String.format("%02x ", b));
        }
        else{
            for(byte b : in)
                builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public  static String bytesToHex(byte[] in){
        return bytesToHex(in,true);
    }

    //Convert byte array to numerical string
    public static String bytesToString(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(b+" ");
        }
        return builder.toString();
    }

    //convert bytes to beacon UID
    public static void bytesToBeacon(byte[] beacBytes, int rssi){
        BleAdvertisement advert = new BleAdvertisement(beacBytes);
        for (Pdu pdu : advert.getPdus()) {

            if (pdu.getType() == Pdu.GATT_SERVICE_UUID_PDU_TYPE) {
                if(pdu.getStartIndex()==9 && pdu.getEndIndex()==28 && pdu.getDeclaredLength()==21) {
                    /*EddystoneUID beac = new EddystoneUID(beacBytes, 13, 28,0);
                    App.addEddyStone(beac);
                    Log.d("namespace", beac.getNamespace());
                    Log.d("instance", beac.getInstance());
                    Log.d("rssi",rssi+"");*/
                    String EUID = Utilities.bytesToHex(Arrays.copyOfRange(beacBytes,13,29),false);
                    Log.d("EUID",EUID);
                    App.addEddyStone(EUID,rssi);
                }
                else if(pdu.getStartIndex()==9 && pdu.getDeclaredLength()==17){
                    if(beacBytes[11]==0x10) {
                        byte[] urlBytes = Arrays.copyOfRange(beacBytes, 13, pdu.getEndIndex() + 1);
                        Log.d("url", UrlBeaconUrlCompressor.uncompress(urlBytes));
                    }
                }
                break;
            }
        }
    }

    public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> map){
        Comparator<String> comparator = new ValueComparator(map);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(map);
        return result;
    }

    public static void sortBeaconsByRssi() {
        App.tList = sortMapByValue(App.list);
    }

    public static void showOfferWithLowestRssi(Context context) {
        if(!App.tList.isEmpty()){
           if (App.lastUID != App.tList.firstKey()) {
               Log.d("first key",App.tList.firstKey());

               if(App.lastUID==null)
               Log.d("lastUID","null");
               else
               Log.d("lastUID",App.lastUID);

               App.lastUID = App.tList.firstKey();
               NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
               builder.setSmallIcon(R.mipmap.ic_launcher);
               builder.setContentTitle("Addidas");
               builder.setContentText(App.tList.firstKey());
               builder.setAutoCancel(true);
               Intent i = new Intent(context,MainActivity.class);


               PendingIntent pi = PendingIntent.getActivity(context,342,i,PendingIntent.FLAG_ONE_SHOT);
               builder.setContentIntent(pi);
               NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
               nm.notify(15,builder.build());
           }
        }
        else {
            Log.d("tlist","empty");
        }
    }
}
