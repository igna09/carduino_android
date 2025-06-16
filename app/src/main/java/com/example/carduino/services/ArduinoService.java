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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.renderscript.RenderScript;
import android.util.Log;
import android.widget.Toast;

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
import com.example.carduino.receivers.canbus.factory.CanbusActions;
import com.example.carduino.shared.MyApplication;
import com.example.carduino.shared.models.AppToOpen;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.AppSwitchSingleton;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.CarStatusSingleton;
import com.example.carduino.shared.singletons.ContextsSingleton;
import com.example.carduino.shared.singletons.LoggerSingleton;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.singletons.TripSingleton;
import com.example.carduino.shared.utilities.ArduinoMessageUtilities;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.hoho.android.usbserial.driver.SerialTimeoutException;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import me.aflak.arduino.Arduino;

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
//                        onArduinoMessage("0;14;" + getFloatRandomNumber(11, 14) + ";");
//                        onArduinoMessage("1;1;FALSE;");
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
//                        if(counter % 2 == 0) {
//                            onArduinoMessage("MEDIA_CONTROL;VOLUME_UP;0;");
//                        } else {
//                            onArduinoMessage("MEDIA_CONTROL;VOLUME_DOWN;0;");
//                        }
//                        onArduinoMessage("CAR_STATUS;INTERNAL_LUMINANCE;" + getIntegerRandomNumber(0, 1000));
//                    }
//                    if(counter % 15 == 0) {
//                        onArduinoMessage("MEDIA_CONTROL;LONG_PRESS;0;");
//                    }
//                    if(counter == 10) {
//                        onArduinoMessage("EVENT;BLE_PAIRING_CODE;123456;");
//                    }
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

    @Override
    public void onCreate() {
        super.onCreate();

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
                TripSingleton.invalidate();
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

            this.locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Handle location updates here
                    LoggerUtilities.logMessage("Location changed: " + location.toString());
                    Toast.makeText(getApplicationContext(), "Location changed: " + location, Toast.LENGTH_LONG).show();

                    // TEMPORARY: AS CAR CANBUS IS NOT READING
                    Value value = CarStatusFactory.getCarStatusValue("SPEED", Float.valueOf(location.getSpeed() * 3.6f).toString());
                    if (value != null) {
                        CarStatusSingleton.getInstance().getCarStatus().putValue(value);
                    }

                    ArduinoService.this.fetchSpeedLimit(location.getLatitude(), location.getLongitude());
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
    }

    public void onArduinoMessage(String message) {
        try {
            if(!message.trim().isEmpty()) {
                String[] splittedMessage = ArduinoMessageUtilities.parseArduinoMessage(message.trim());

                if (splittedMessage.length == 3) {
                    boolean isNumericMode = ArduinoMessageUtilities.isNumeric(splittedMessage[0]);

                    if(isNumericMode) {
                        splittedMessage[0] = ((CanbusActions) CanbusActions.getEnumById(Integer.parseInt(splittedMessage[0]))).name();
                        if(CanbusActions.valueOf(splittedMessage[0]).getActionEnumById() != null) {
                            splittedMessage[1] = ((Enum<?>) CanbusActions.valueOf(splittedMessage[0]).getActionEnumById().apply(Integer.parseInt(splittedMessage[1]))).name();
                        }
                    }

                    boolean existsAction;
                    try {
                        CanbusActions.valueOf(splittedMessage[0]);
                        existsAction = true;
                    } catch (IllegalArgumentException e) {
                        existsAction = false;
                    }

//                    try {
//                        CanbusActions.valueOf(splittedMessage[0]);
//                    } catch (IllegalArgumentException e) {
//                        existsAction = false;
//                    }

                    if (existsAction) {
                        ArduinoMessage arduinoMessage = new ArduinoMessage(CanbusActions.valueOf(splittedMessage[0]), splittedMessage[1], splittedMessage[2]);

                        LoggerUtilities.logArduinoMessage("ArduinoService", "receiving " + arduinoMessage.toSerialString());
                        ArduinoSingleton.getInstance().getCircularArrayList().add(arduinoMessage.toSerialString());

                        ArduinoMessageExecutorInterface action = null;
                        action = (ArduinoMessageExecutorInterface) arduinoMessage.getAction().getClazz().newInstance();
                        action.execute(arduinoMessage);
                    } else {
                        LoggerUtilities.logArduinoMessage("ArduinoService", "Action not existing " + message);
                    }
                } else {
                    LoggerUtilities.logArduinoMessage("ArduinoService", "malformed message " + StringEscapeUtils.escapeJava(message));
                }
            }
        } catch (Exception e) {
            LoggerUtilities.logException(e);
        }
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

    private void fetchSpeedLimit(double latitude, double longitude) {
        new Thread(() -> {
            try {
                String urlString = "https://overpass-api.de/api/interpreter?data=[out:json];way[\"maxspeed\"](around:50," + latitude + "," + longitude + ");out;";
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
                String speedLimit = null;
                if (elements.length() > 0) {
                    speedLimit = elements.getJSONObject(0).getJSONObject("tags").optString("maxspeed", null);
                }
                if(speedLimit != null) {
                    SharedDataSingleton.getInstance().setRoadLimit(Float.valueOf(speedLimit));
                }

                //runOnUiThread(() -> speedLimitTextView.setText("Limite di velocità: " + speedLimit + " km/h"));
            } catch (Exception e) {
                Log.e("SpeedLimit", "Errore nel recupero del limite di velocità", e);
            }
        }).start();
    }
}
