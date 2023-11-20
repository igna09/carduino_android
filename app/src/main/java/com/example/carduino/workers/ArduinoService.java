package com.example.carduino.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.carduino.R;
import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.receivers.canbus.factory.CanbusActions;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.singletons.ContextsSingleton;
import com.example.carduino.shared.singletons.Logger;
import com.example.carduino.shared.utilities.ArduinoMessageUtilities;

import java.io.PrintWriter;
import java.io.StringWriter;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class ArduinoService extends Service implements ArduinoListener {
    private Arduino arduino;
    private class ArduinoRunnable implements  Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
//                            Log.e("Service", "Service is running...");
//                            Logger.getInstance().log("Service is running...");
                try {
                    long s = getRandomNumber(1, 10) * 1000;
                    Log.d("sleep", "Sleeping for " + s);
                    Thread.sleep(s);
                    onArduinoMessage(("CAR_STATUS;INTERNAL_LUMINANCE;" + getRandomNumber(0, 5000)).getBytes());
//                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Logger.getInstance().log("service stopped");
//                    e.printStackTrace();
                    return;
                }
            }
            Logger.getInstance().log("service stopped");
        }

        public int getRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }
    }

    private static Thread t;

    @Override
    public void onCreate() {
        super.onCreate();

        ContextsSingleton.getInstance().setServiceContext(this);

        arduino = new Arduino(this);
        arduino.setBaudRate(115200);
        arduino.addVendorId(6790);
        arduino.addVendorId(0); //just for local test
        arduino.setArduinoListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals("STOP_FOREGROUND")) {
            t.interrupt();
            stopForeground(true);
            stopSelfResult(startId);
            return START_NOT_STICKY;
        } else if(intent.getAction() != null && intent.getAction().equals("START_FOREGROUND")) {
            t = new Thread(new ArduinoRunnable());
            t.start();

            final String CHANNELID = "Foreground Service ID";
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );

//            Intent stopIntent = new Intent(this, ArduinoService.class);
//            stopIntent.putExtra("action", "STOP");

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNELID)
                    .setContentText("Service is running")
                    .setContentTitle("Service enabled")
                    .setSmallIcon(R.drawable.ic_launcher_background);
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
//                    .addAction(android.R.drawable.ic_delete, "STOP", PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE));

            startForeground(1001, notification.build());

            super.onStartCommand(intent, flags, startId);
        }
        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        if(t != null) {
            t.interrupt();
        }
        arduino.close();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onArduinoAttached(UsbDevice device) {
        Log.d("ArduinoWorker", "arduino attached");
        Logger.getInstance().log("arduino attached");
        arduino.open(device);
    }

    @Override
    public void onArduinoDetached() {
        Logger.getInstance().log("arduino detached");
        Log.d("ArduinoWorker", "arduino detached");
    }

    /**
     * Every message received from Arduino MUST be in this format:
     * CanbusActions;payload;
     * eg.: CAR_STATUS;BRIGHTNESS;2700;
     * @param bytes
     */
    @Override
    public void onArduinoMessage(byte[] bytes) {
        Logger.getInstance().log("onArduinoMessage");
        String message = new String(bytes);
        Log.d("ArduinoWorker", "arduino message: " + message);
        Logger.getInstance().log("arduino message: " + message);

        try {
            String[] splittedMessage = ArduinoMessageUtilities.parseArduinoMessage(message);

            if (splittedMessage.length == 3) {
                Logger.getInstance().log("good message, sending it");
                ArduinoMessage arduinoMessage = new ArduinoMessage(CanbusActions.valueOf(splittedMessage[0]), splittedMessage[1], splittedMessage[2]);
                ArduinoMessageExecutorInterface action = null;
                action = (ArduinoMessageExecutorInterface) arduinoMessage.getAction().getClazz().newInstance();
                action.execute(arduinoMessage);
            } else {
                Logger.getInstance().log("malformed message");
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Logger.getInstance().log(sw.toString());
        }
    }

    @Override
    public void onArduinoOpened() {
        String str = "arduino opened...";
//        arduino.send(str.getBytes());
        Log.d("ArduinoWorker", str);
        Logger.getInstance().log(str);
    }

    @Override
    public void onUsbPermissionDenied() {
        Log.d("ArduinoWorker", "Permission denied. Attempting again in 3 sec...");
        Logger.getInstance().log("Permission denied. Attempting again in 3 sec...");

        if (Looper.myLooper() == null) {
            Looper.prepare();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arduino.reopen();
            }
        }, 3000);
    }
}