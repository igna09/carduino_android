package com.example.carduino.services;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.carduino.R;
import com.example.carduino.arduinolistener.Constants;
import com.example.carduino.arduinolistener.CustomProber;
import com.example.carduino.arduinolistener.SerialListener;
import com.example.carduino.arduinolistener.SerialSocket;
import com.example.carduino.arduinolistener.StringBuffer;
import com.example.carduino.arduinolistener.TextUtil;
import com.example.carduino.carduino.CarduinoActivity;
import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.MyApplication;
import com.example.carduino.shared.models.AppToOpen;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.Event;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.AppSwitchSingleton;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.CarStatusSingleton;
import com.example.carduino.shared.singletons.ContextsSingleton;
import com.example.carduino.shared.singletons.LoggerSingleton;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.singletons.TripHistorySingleton;
import com.example.carduino.shared.utilities.ArduinoMessageUtilities;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.example.carduino.speedlimit.DeadReckoningTracker;
import com.example.carduino.speedlimit.SpeedLimitManager;
import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArduinoService extends Service implements SerialListener {
    private class ArduinoRunnable implements Runnable {
        Integer counter = 0;
        @Override
        public void run() {
            while (Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
//                            Log.e("Service", "Service is running...");
//                            Logger.getInstance().log("Service is running...");
                try {
//                    onSerialRead("1;1;FALSE;\r\n0;7;1017.90;\r\n1;2;FALSE;\r\n".getBytes());
//                    onSerialRead("1;2;FALSE;\r\n".getBytes());
//                    onSerialRead("1;3;FALSE;\r\n".getBytes());
//                    onSerialRead("0;7;1017.90;\r\n".getBytes());
//                    onSerialRead("1;1;FALSE;\r\n".getBytes());
//                    onSerialRead("1;2;FALSE;\r\n".getBytes());
//                    onSerialRead("1;3;FALSE;\r\n".getBytes());
//                    onSerialRead("0;7;1017.90;\r\n".getBytes());
//                    onSerialRead("1;1;FALSE;\r\n".getBytes());
//                    onSerialRead("1;2;FALSE;\r\n".getBytes());
//                    onSerialRead("1;3;FALSE;\r\n".getBytes());
//                    onSerialRead("0;7;1017.90;\r\n".getBytes());
//                    onSerialRead("4;3;8@TRUE;\r\n".getBytes());
//                    long s = getIntegerRandomNumber(1, 10) * 1000;
//                    Log.d("sleep", "Sleeping for " + s);
//                    Thread.sleep(s);
//                        onArduinoMessage("CAR_STATUS;SPEED;" + getIntegerRandomNumber(0, 200));
//                        onArduinoMessage("0;2;" + getIntegerRandomNumber(0, 200));
//                        onArduinoMessage("0;13;" + getFloatRandomNumber(0, 10));
//                        onArduinoMessage("0;7;" + getFloatRandomNumber(1000, 2500));
//                    onArduinoMessage("BATTERY_VOLTAGE;" + getFloatRandomNumber(11, 14) + ";");
//                    onArduinoMessage("SPEED_LIMIT_SET;" + getIntegerRandomNumber(0, 200));
//                        onArduinoMessage("45;0;150.0;");
//                    onArduinoMessage("1;2;FALSE;");
//                    onArduinoMessage("1;3;FALSE;");
//                        onArduinoMessage("1;4;true;");
//                    onArduinoMessage("1;0;true;");
//                    onArduinoMessage("READ_SETTINGS;RESTART;false;");
//                    onArduinoMessage("0;7;1017.90;");
//                    onArduinoMessage("READ_SETTING;OTA_MODE;false;");
//                    onArduinoMessage("CAR_STATUS;BATTERY_VOLTAGE;12.45;");
//                    if(counter >= 5)
//                        onArduinoMessage("CAR_STATUS;ENGINE_RPM;" + getIntegerRandomNumber(900, 4000));
//                    if(counter % 5 == 0) {
//                        if(counter % 2 == 0)  {
//                            onArduinoMessage("MEDIA_CONTROL;VOLUME_UP;0;");
//                        } else {
//                            onArduinoMessage("MEDIA_CONTROL;VOLUME_DOWN;0;");
//                        }
//                        onArduinoMessage("CAR_STATUS;INTERNAL_LUMINANCE;" + getIntegerRandomNumber(0, 1000));
//                    }
//                    if(counter % 15 == 0) {
//                        onArduinoMessage("MEDIA_CONTROL;LONG_PRESS;0;");
//                    }
//                    if(counter % 15 == 0) {
//                        onArduinoMessage("EVENT;BLE_PAIRING_CODE;123456;");
                        //onArduinoMessage("READ_SETTING;SEND_ALL_MESSAGES_TO_RADIO;FALSE;");
//                    }
                    if(counter % 15 == 0 && counter % 2 == 0) {
                        onArduinoMessage("ENGINE_RPM;0;");
                    } else if(counter % 15 == 0 && counter % 2 == 1) {
                        onArduinoMessage("ENGINE_RPM;3000;");
                    }
                    onArduinoMessage("SPEED;"+getIntegerRandomNumber(100, 130));
                    onArduinoMessage("FUEL_CONSUMPTION;"+getFloatRandomNumber(10, 20));
//                    onArduinoMessage("47;" + getIntegerRandomNumber(0, 200));
                    Thread.sleep(1000);
                    counter++;
                } catch (InterruptedException e) {
                    LoggerUtilities.logMessage("ArduinoService", "keepAliveThread interrupted while sleeping");
                }
            }
            LoggerUtilities.logMessage("ArduinoService", "service stopped");
        }

        public Integer getIntegerRandomNumber(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }

        public Float getFloatRandomNumber(int min, int max) {
            return (float) ((Math.random() * (max - min)) + min);
        }
    }

    private static Thread keepAliveThread;

    class SerialBinder extends Binder {
        ArduinoService getService() { return ArduinoService.this; }
    }
    private final IBinder binder;

    private SerialSocket socket;
    private CarduinoActivity.Connected connected;

    private final BroadcastReceiver broadcastReceiver;
    private Integer deviceIdToConnect;

    private final StringBuffer buffer;

    private Thread connectThread;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final Integer LOCATION_INTERVAL = 5000;
    private final ExecutorService mapExecutor = Executors.newSingleThreadExecutor();
    private SpeedLimitManager speedLimitManager;
    private DeadReckoningTracker deadReckoning;
    private boolean isSpeedLimitReady = false;
    private Location previousLocation = null;
    private double lastKnownHeading = -1;
    private static final float MIN_SPEED_FOR_BEARING_MS = 1.0f; // sotto 1 m/s il bearing è rumore
    private static final long DEAD_RECKONING_INTERVAL_MS = 200;
    private Handler deadReckoningHandler = new Handler(Looper.getMainLooper());


    public ArduinoService() {
        binder = new SerialBinder();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(Constants.INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    Boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    attemptConnect(deviceIdToConnect, granted);
                }
            }
        };

        /*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        BroadcastReceiver mReceiver = new BootReceiver();
        registerReceiver(mReceiver, filter);*/

        buffer = new StringBuffer();

        ArduinoSingleton.getInstance().setArduinoService(this);
    }

    private void scanAndConnectExistingDevices() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        boolean found = false;
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getVendorId() == 0x303A && device.getProductId() == 0x1001) {
                LoggerUtilities.logMessage("ArduinoService::scanAndConnectExistingDevices()", "found device, connecting");
                attemptConnect(device.getDeviceId(), false);
                found = true;
                break;
            }
        }
        if (!found) {
            LoggerUtilities.logMessage("ArduinoService::scanAndConnectExistingDevices()", "no matching device found at startup");
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.scanAndConnectExistingDevices();

//        LoggerUtilities.logMessage("ArduinoService::onCreate()", "onCreate()");
        BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LoggerUtilities.logMessage("ArduinoService screenOnReceiver::onCreate()", "screen on");

                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                // The first in the list of RunningTasks is always the foreground task.
                ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
//                PackageManager pm = context.getPackageManager();
//                PackageInfo foregroundAppPackageInfo = null;
//                try {
//                    foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
//                } catch (PackageManager.NameNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//                String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
//
//                if(foregroundTaskAppName.equals("Carduino")) {
//                    LoggerUtilities.logMessage("ArduinoService screenOnReceiver::onCreate()", "carduino app in foreground");
//                }
                LoggerUtilities.logMessage("ArduinoService screenOnReceiver::onCreate()", "AppToOpen.CARDUINO.getPackageName().equals(foregroundTaskPackageName) " + (AppToOpen.CARDUINO.getPackageName().equals(foregroundTaskPackageName) ? "true" : "false") + " ((MyApplication) getApplicationContext()).isShowingApplication() " + (((MyApplication) getApplicationContext()).isShowingApplication() ? "true" : "false"));
                Toast.makeText(context, "AppToOpen.CARDUINO.getPackageName().equals(foregroundTaskPackageName) " + (AppToOpen.CARDUINO.getPackageName().equals(foregroundTaskPackageName) ? "true" : "false") + " ((MyApplication) getApplicationContext()).isShowingApplication() " + (((MyApplication) getApplicationContext()).isShowingApplication() ? "true" : "false"), Toast.LENGTH_LONG).show();
                if(!AppToOpen.CARDUINO.getPackageName().equals(foregroundTaskPackageName)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(AppToOpen.CARDUINO.getPackageName());
                    if (launchIntent != null) {
                        startActivity(launchIntent); //null pointer check in case package name was not found
                    }
                }
            }
        };
        IntentFilter screenOnFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnReceiver, screenOnFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Api
     */
    public void connect(SerialSocket socket) throws IOException {
        socket.connect(this);
        this.socket = socket;
        connected = CarduinoActivity.Connected.True;
    }

    public void disconnect() {
        connected = CarduinoActivity.Connected.False;
        if(socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    /**
     * SerialListener
     */
    public void onSerialConnect() {
        LoggerUtilities.logMessage("ArduinoService::onSerialConnect()", "");
    }

    /**
     * reduce number of UI updates by merging data chunks.
     * Data can arrive at hundred chunks per second, but the UI can only
     * perform a dozen updates if receiveText already contains much text.
     *
     * On new data inform UI thread once (1).
     * While not consumed (2), add more data (3).
     */
    public void onSerialRead(byte[] data) {
//        LoggerUtilities.logMessage("ArduinoService::onSerialRead()", "");
        buffer.addData(new String(data));
        String line = buffer.readNewLine();
        while(line != null) {
//            if (connected == CarduinoActivity.Connected.True) {
                synchronized (this) {
                    onArduinoMessage(line);
                }
//            }
            line = buffer.readNewLine();
        }
    }

    public void write(byte[] data) throws IOException {
        if(connected != CarduinoActivity.Connected.True)
            throw new IOException("not connected");
        socket.write(data);
    }

    public void onSerialIoError(Exception e) {
        LoggerUtilities.logMessage("ArduinoService::onSerialIoError()", "");
//        LoggerUtilities.logException(e);
        if(connected == CarduinoActivity.Connected.True) {
            synchronized (this) {
                connected = CarduinoActivity.Connected.False;
            }
        }
        startConnectThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals("STOP_FOREGROUND")) {
            LoggerUtilities.logMessage("service", "Stopping");

            if(keepAliveThread != null && keepAliveThread.isAlive() && !keepAliveThread.isInterrupted()) {
                keepAliveThread.interrupt();
            }

            if(connectThread != null && connectThread.isAlive() && !connectThread.isInterrupted()) {
                connectThread.interrupt();
            }

            stopForeground(true);
            stopSelfResult(startId);

            if(((MyApplication) getApplicationContext()).getForegroundActivity() != null) {
                MyApplication myApplication = ((MyApplication) getApplicationContext());
                myApplication.getForegroundActivity().finishAndRemoveTask();

                ArduinoSingleton.invalidate();
                AppSwitchSingleton.invalidate();
                SettingsSingleton.invalidate();
                CarStatusSingleton.invalidate();
                SharedDataSingleton.invalidate();
                LoggerSingleton.invalidate();
                TripHistorySingleton.invalidate();
                ContextsSingleton.invalidate();
            }

            return START_NOT_STICKY;
        } else if(intent == null || (intent != null && intent.getAction() == null || (intent.getAction().equals("START_FOREGROUND")))) {
            LoggerUtilities.logMessage("ArduinoService", "Starting service");

            keepAliveThread = new Thread(new ArduinoRunnable());
            keepAliveThread.start();

            final String CHANNELID = "Foreground Service ID";
            NotificationChannel channel = new NotificationChannel(
                    CHANNELID,
                    CHANNELID,
                    NotificationManager.IMPORTANCE_LOW
            );

            Intent stopIntent = new Intent(this, ArduinoService.class);
            stopIntent.setAction("STOP_FOREGROUND");

            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNELID)
                    .setContentText("Service is running, expand me to stop")
                    .setContentTitle("Carduino service")
                    .setSmallIcon(R.drawable.baseline_directions_car_24)
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
                    .addAction(android.R.drawable.ic_delete, "STOP", PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE));

            startForeground(1001, notification.build());

//            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    "Carduino::MyWakelockTag");
//            wakeLock.acquire();

            registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_ACTION_GRANT_USB));

            super.onStartCommand(intent, flags, startId);

            if(!isConnected()) {
                startConnectThread();
            }

            // Inizializzazione
            speedLimitManager = new SpeedLimitManager();
            deadReckoning = new DeadReckoningTracker();

            File binFile = new File(getExternalFilesDir(null), "speedlimits.bin");

            speedLimitManager.load(binFile, () -> {
                isSpeedLimitReady = true;
                LoggerUtilities.logMessage("SpeedLimitManager caricato con successo!");
            });

            this.locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double heading = computeHeading(location, previousLocation);
                    previousLocation = location;

                    if (heading >= 0) {
                        lastKnownHeading = heading;
                    }

                    double accuracy = location.hasAccuracy() ? location.getAccuracy() : 0;

                    deadReckoning.onGpsFix(location, lastKnownHeading);
                    ArduinoService.this.fetchRoadInfo(location.getLatitude(), location.getLongitude(), lastKnownHeading, accuracy);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {
                    LoggerUtilities.logMessage("Provider enabled: " + provider);
                }
                @Override
                public void onProviderDisabled(String provider) {
                    LoggerUtilities.logMessage("Provider disabled: " + provider);
                }
            };

            this.getLocation();

            this.startDeadReckoningLoop();

            return Service.START_STICKY_COMPATIBILITY;
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        LoggerUtilities.logMessage("service", "onDestroy()");

        unregisterReceiver(broadcastReceiver);
        disconnect();

        if(connectThread != null && connectThread.isAlive() && !connectThread.isInterrupted()) {
            connectThread.interrupt();
        }

        if(keepAliveThread != null && keepAliveThread.isAlive() && !keepAliveThread.isInterrupted()) {
            keepAliveThread.interrupt();
        }

        super.onDestroy();

        if (mapExecutor != null && !mapExecutor.isShutdown()) {
            mapExecutor.shutdown();
        }
    }

    public void onArduinoMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        try {
            // 1. Il costruttore analizza l'ID/Nome, valida e converte i parametri in automatico
            ArduinoMessage arduinoMessage = new ArduinoMessage(message.trim());

            // 2. Log e salvataggio nel buffer circolare
            LoggerUtilities.logArduinoMessage("ArduinoService", "receiving " + arduinoMessage.toHumanString());
            ArduinoSingleton.getInstance().getCircularArrayList().add(arduinoMessage.toHumanString());

            // 3. Recupero ed esecuzione dinamica dell'Executor associato all'evento
            Class<? extends ArduinoMessageExecutorInterface> executorClass = arduinoMessage.getEvent().getExecutorClass();
            if (executorClass != null) {
                ArduinoMessageExecutorInterface action = executorClass.newInstance();
                action.execute(arduinoMessage);
            } else {
                LoggerUtilities.logArduinoMessage("ArduinoService", "Nessun executor mappato per l'evento: " + arduinoMessage.getEvent().name());
            }

        } catch (IllegalArgumentException e) {
            // Cattura eventi sconosciuti, formati errati o parametri non conformi
            LoggerUtilities.logArduinoMessage("ArduinoService", "Messaggio non valido o sconosciuto: " + message + " -> " + e.getMessage());
        } catch (Exception e) {
            // Cattura problemi imprevisti (es. fallimento riflessione istanza)
            LoggerUtilities.logException(e);
        }
    }

    private double computeHeading(Location current, Location previous) {
        // 1. Preferisci il bearing calcolato dal GPS stesso (Doppler), se affidabile
        if (current.hasBearing() && current.hasSpeed() && current.getSpeed() > MIN_SPEED_FOR_BEARING_MS) {
            return current.getBearing();
        }

        // 2. Fallback: bearing geometrico tra due fix successivi
        if (previous != null) {
            float distance = previous.distanceTo(current);
            // Se il veicolo si è mosso abbastanza da dare un bearing sensato
            if (distance > 3.0f) { // metri, evita rumore GPS a fermo
                return bearingBetween(previous.getLatitude(), previous.getLongitude(),
                        current.getLatitude(), current.getLongitude());
            }
        }

        // 3. Veicolo fermo o primo fix: nessun heading affidabile
        return -1;
    }

    private static double bearingBetween(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    private void startDeadReckoningLoop() {
        deadReckoningHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (deadReckoning.isGpsStale()) {
                    Value lastSpeed = null;
                    if(
                            CarStatusSingleton.getInstance().getCarStatus() != null
                                    && CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues() != null
                                    && CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues().containsKey(CarStatusEnum.SPEED.name())
                    ) {
                        lastSpeed = CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues().get(CarStatusEnum.SPEED.name());
                    }
                    if(lastSpeed != null && lastSpeed.getValue() != null) {
                        int vehicleSpeedKmh = (Integer) lastSpeed.getValue();

                        double[] estimatedPos = deadReckoning.estimatePosition(vehicleSpeedKmh);
                        if (estimatedPos != null) {
                            fetchRoadInfo(estimatedPos[0], estimatedPos[1], lastKnownHeading, 20.0);
                        }
                    }
                }
                deadReckoningHandler.postDelayed(this, DEAD_RECKONING_INTERVAL_MS);
            }
        }, DEAD_RECKONING_INTERVAL_MS);
    }

    public void sendMessageToArduino(String message) {
//        LoggerUtilities.logArduinoMessage("sending", message);
        if(connected != CarduinoActivity.Connected.True) {
            LoggerUtilities.logMessage("not connected");
            return;
        }
        try {
            byte[] data;
            data = (message + TextUtil.newline_lf).getBytes();
            this.write(data);
        } catch (SerialTimeoutException e) {
            LoggerUtilities.logMessage("write timeout: " + e.getMessage());
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    public void startConnectThread() {
        connectThread = new Thread(() -> {
            LoggerUtilities.logMessage("startConnectThread()", "starting connection thread");
            while(!isConnected() && Thread.currentThread().isAlive() && !Thread.currentThread().isInterrupted()) {
                UsbDevice device = null;
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                for(UsbDevice v : usbManager.getDeviceList().values()) {
                    if (v.getVendorId() == 6790 && v.getProductId() == 29987) {
                        device = v;
                    }
                }

                if(device != null) {
                    attemptConnect(device.getDeviceId(), false);
                }

                if(!isConnected()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LoggerUtilities.logException(e);
                    }
                } else {
                    LoggerUtilities.logMessage("startConnectThread()", "connected");
                }
            }
        });

        connectThread.start();
    }

    public Boolean attemptConnect(Integer deviceId, Boolean granted) {
        LoggerUtilities.logMessage("ArduinoService::connectDevice()", "begin");
        deviceIdToConnect = deviceId;

        Integer portNum = null;
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        for(UsbDevice v : usbManager.getDeviceList().values())
            if(v.getDeviceId() == deviceId) {
                device = v;
                portNum = 0;
            }
        if(device == null) {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()", "connection failed: device not found");
            return false;
        } else {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()", "found device " + device.getDeviceName());
        }

        LoggerUtilities.logMessage("USB", "VID=" + device.getVendorId() + " PID=" + device.getProductId());

        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if(driver == null) {
            driver = CustomProber.getCustomProber().probeDevice(device);
        }
        if(driver == null) {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()", "connection failed: no driver for device");
            return false;
        } else {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()", "found driver " + driver);
        }

        if(driver.getPorts().size() < portNum) {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()","connection failed: not enough ports at device");
            return false;
        }
        UsbSerialPort usbSerialPort = driver.getPorts().get(portNum);

        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if(usbConnection == null && (granted == null || granted == false) && !usbManager.hasPermission(driver.getDevice())) {
            LoggerUtilities.logMessage("ArduinoService::connectDevice()","requesting permissions");
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return false;
        }
        if(usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                LoggerUtilities.logMessage("ArduinoService::connectDevice()", "connection failed: permission denied");
            else
                LoggerUtilities.logMessage("ArduinoService::connectDevice()", "connection failed: open failed");
            return false;
        }

        try {
            usbSerialPort.open(usbConnection);
            try {
                usbSerialPort.setParameters(115200, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            } catch (UnsupportedOperationException e) {
                LoggerUtilities.logException(e);
            }
            SerialSocket socket = new SerialSocket(getApplicationContext(), usbConnection, usbSerialPort);
            this.connect(socket);
        } catch (Exception e) {
            LoggerUtilities.logException(e);
            return false;
        }
        LoggerUtilities.logMessage("ArduinoService::connectDevice()", "connected");
        return true;
    }

    public Boolean isConnected() {
        return connected == CarduinoActivity.Connected.True;
    }

    private void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,  ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, 0, locationListener, Looper.getMainLooper());
        }
    }

    private void fetchRoadInfo(double lat, double lon, double headingDeg, double gpsAccuracyM) {
        if (!isSpeedLimitReady) return;

        Integer limit = speedLimitManager.getCurrentSpeedLimit(lat, lon, headingDeg, gpsAccuracyM);

        Integer previousSpeedLimit = null;
        if(
                CarStatusSingleton.getInstance().getCarStatus() != null
                        && CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues() != null
                        && CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues().containsKey(CarStatusEnum.SPEED_LIMIT.name())
        ) {
            Value previousSpeedLimitValue = CarStatusSingleton.getInstance().getCarStatus().getCarStatusValues().get(CarStatusEnum.SPEED_LIMIT.name());
            if(previousSpeedLimitValue != null && previousSpeedLimitValue.getValue() != null) {
                previousSpeedLimit = (Integer) previousSpeedLimitValue.getValue();
            }
        }

        if (limit != null && !limit.equals(previousSpeedLimit)) {
            // Creo il valore con la chiave "SPEED_LIMIT" per il tuo CarStatus
            Value speedLimitValue = CarStatusFactory.getCarStatusValue(CarStatusEnum.SPEED_LIMIT.name(), limit.toString());

            if (speedLimitValue != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(speedLimitValue);
            }

            ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.SPEED_LIMIT_SET, limit));
            // dispatch del valore verso il resto del sistema (CAN, UI, ecc.)
            // LoggerUtilities.logMessage("Limite di velocità: " + limit + " km/h");
        }
    }

    private static @NonNull JSONArray getJsonArray(double latitude, double longitude) throws IOException, JSONException {
        String urlString = "https://overpass-api.de/api/interpreter?data=[out:json];way[\"maxspeed\"][\"name\"](around:50," + latitude + "," + longitude + ");out;";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        Scanner scanner = new Scanner(url.openStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        JSONArray elements = jsonObject.getJSONArray("elements");
        return elements;
    }
}
