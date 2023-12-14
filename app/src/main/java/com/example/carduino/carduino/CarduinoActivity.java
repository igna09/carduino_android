package com.example.carduino.carduino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.carduino.R;
import com.example.carduino.arduinolistener.SerialSocket;
import com.example.carduino.canbus.fragments.Canbus;
import com.example.carduino.carstatus.Carstatus;
import com.example.carduino.settings.fragments.Settings;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.ContextsSingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.example.carduino.shared.utilities.PermissionUtilities;
import com.example.carduino.test.Test;
import com.example.carduino.services.ArduinoService;
import com.example.carduino.arduinolistener.CustomProber;
import com.example.carduino.arduinolistener.Constants;
import com.google.android.material.navigationrail.NavigationRailView;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarduinoActivity extends AppCompatActivity {
    class MyMenuItem {
        Integer id;
        String title;
        Integer icon;
        Class clazz;

        public MyMenuItem(Integer id, String title, Integer icon, Class clazz) {
            this.id = id;
            this.title = title;
            this.clazz = clazz;
            this.icon = icon;
        }

        public Integer getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Class getClazz() {
            return clazz;
        }

        public Integer getIcon() {
            return icon;
        }
    }

    private enum Connected { False, Pending, True }

    List<MyMenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoggerUtilities.logMessage("CarduinoActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carduino);

        ContextsSingleton.getInstance().setMainActivityContext(this);
        ContextsSingleton.getInstance().setApplicationContext(getApplication());

        getSupportFragmentManager().beginTransaction().replace(R.id.carduino_main_view, new Carstatus()).commit();

        PermissionUtilities.requestMissingPermissions();
        if(!foregroundServiceRunning()) {
            LoggerUtilities.logMessage("Carduino", "service not running");
            if(PermissionUtilities.haveAllPermissions()) {
                LoggerUtilities.logMessage("Carduino", "application does have all permissions, start service");
                startArduinoService();
            } else {
                LoggerUtilities.logMessage("Carduino", "application does not have all permissions, requesting them");
                PermissionUtilities.requestMissingPermissions();
            }
        }

//        if(getIntent() != null) {
//            UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
//            if(device != null)
//                Toast.makeText(this, Integer.valueOf(device.getDeviceId()).toString(), Toast.LENGTH_SHORT).show();
//            else
//                Toast.makeText(this, "no device", Toast.LENGTH_SHORT).show();
//        }

        menuItems = new ArrayList<>();
        menuItems.add(new MyMenuItem(0, "Carstatus", R.drawable.baseline_directions_car_24, Carstatus.class));
        menuItems.add(new MyMenuItem(2, "CAN bus", R.drawable.baseline_usb_24, Canbus.class));
        menuItems.add(new MyMenuItem(1, "Settings", R.drawable.baseline_settings_24, Settings.class));
        menuItems.add(new MyMenuItem(3, "Test", R.drawable.baseline_tips_and_updates_24, Test.class));

        NavigationRailView navigationView = findViewById(R.id.navigation_rail);
        for(int i = 0; i < menuItems.size(); i++) {
            MyMenuItem e = menuItems.get(i);
            MenuItem menuItem = navigationView.getMenu().add(Menu.NONE, e.getId(), i, e.title);
            menuItem.setIcon(e.getIcon());
        }
        navigationView.setOnItemSelectedListener(item -> {
            Optional<MyMenuItem> optionalMyMenuItem = menuItems.stream().filter(e -> e.getId() == item.getItemId()).findFirst();
            if(optionalMyMenuItem.isPresent()) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.carduino_main_view, (Fragment) optionalMyMenuItem.get().getClazz().newInstance()).addToBackStack(null).commit();
                    return true;
                } catch (Exception e) {
                    LoggerUtilities.logException(e);
                    return false;
                }
            } else {
                return super.onOptionsItemSelected(item);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoggerUtilities.logMessage("CarduinoActivity", "onDestroy");
    }

    void startArduinoService() {
        Intent serviceIntent = new Intent(this, ArduinoService.class);
        serviceIntent.setAction("START_FOREGROUND");
        startForegroundService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(PermissionUtilities.haveAllPermissions()) {
//            startArduinoService();
        }
    }

    public boolean foregroundServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(ArduinoService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopForegroundService() {
        Intent stopIntent = new Intent(CarduinoActivity.this, ArduinoService.class);
        stopIntent.setAction("STOP_FOREGROUND");
        startService(stopIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if(device != null) {}
//                Toast.makeText(this, Integer.valueOf(device.getDeviceId()).toString(), Toast.LENGTH_SHORT).show();
//                connect(device.getDeviceId());
            else
                Toast.makeText(this, "no device", Toast.LENGTH_SHORT).show();
        }
        super.onNewIntent(intent);
    }
}