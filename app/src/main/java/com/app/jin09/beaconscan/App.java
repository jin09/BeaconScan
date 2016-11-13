package com.app.jin09.beaconscan;

import android.app.Application;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by gautam on 13-11-2016.
 */

public class App extends Application {
    static String lastUID = null;
    static HashMap<String,Integer> list = new HashMap<String, Integer>();
    static TreeMap<String,Integer> tList = new TreeMap<String, Integer>();
    public static void addEddyStone(String EUID,int rssi){
        list.put(EUID,rssi);
    }
}
