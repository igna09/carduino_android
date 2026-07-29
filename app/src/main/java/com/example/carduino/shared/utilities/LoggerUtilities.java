package com.example.carduino.shared.utilities;

import android.util.Log;

import com.example.carduino.shared.singletons.LoggerSingleton;

public class LoggerUtilities {
    public static void logMessage(String message) {
        logMessage("CARDUINO_APP", message);
    }
    public static void logMessage(String label, String message) {
        LoggerSingleton.getInstance().log(label + " - " + message);
        Log.d(label, message);
    }
    //TODO: remove
    public static void logArduinoMessage(String label, String message) {
        LoggerSingleton.getInstance().log("arduino_message", label + " - " + message);
        Log.d(label, message);
    }
    public static void logException(Exception e) {
        LoggerSingleton.getInstance().logException(e);
        //e.printStackTrace();
    }
}
