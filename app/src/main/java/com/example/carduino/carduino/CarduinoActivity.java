package com.example.carduino.carduino;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.carduino.R;
import com.example.carduino.carstatus.Carstatus;
import com.example.carduino.services.ArduinoService;
import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.PropertyChangeListener;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.example.carduino.shared.utilities.PermissionUtilities;
import com.google.android.material.navigationrail.NavigationRailView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CarduinoActivity extends AppCompatActivity {
    class MyMenuItem {
        Integer id;
        String title;
        Integer icon;
        Class clazz;
        Boolean advancedMode;

        public MyMenuItem(Integer id, String title, Integer icon, Class clazz, Boolean advancedMode) {
            this.id = id;
            this.title = title;
            this.clazz = clazz;
            this.icon = icon;
            this.advancedMode = advancedMode;
        }
    }

    public enum Connected { False, Pending, True }
    NavigationRailView navigationView;
    List<MyMenuItem> menuItems;
    private PropertyChangeListener advancedModePropertyChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoggerUtilities.logMessage("CarduinoActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carduino);

        getSupportFragmentManager().beginTransaction().replace(R.id.carduino_main_view, new Carstatus()).commit();

        PermissionUtilities.requestMissingPermissions(this);
        if(!foregroundServiceRunning()) {
            LoggerUtilities.logMessage("Carduino", "service not running");
            if(PermissionUtilities.haveAllPermissions()) {
                LoggerUtilities.logMessage("Carduino", "application does have all permissions, start service");
                startArduinoService();
            } else {
                LoggerUtilities.logMessage("Carduino", "application does not have all permissions, requesting them");
                PermissionUtilities.requestMissingPermissions(this);
            }
        }

        menuItems = new ArrayList<>();
        Arrays.stream(MenuEnum.values()).forEach(menuEnum -> {
            menuItems.add(new MyMenuItem(menuEnum.id, menuEnum.title, menuEnum.iconResource, menuEnum.fragmentClass, menuEnum.advancedMode));
        });

        navigationView = findViewById(R.id.navigation_rail);
        for(int i = 0; i < menuItems.size(); i++) {
            MyMenuItem e = menuItems.get(i);
            MenuItem menuItem = navigationView.getMenu().add(e.advancedMode ? 2 : Menu.NONE, e.id, i, e.title);
            menuItem.setIcon(e.icon);
        }
        Boolean advancedMenu = (Boolean) SettingsSingleton.getInstance().getSettings().get(SettingsEnum.ADVANCED_MODE.name()).getValue();
        setAdvancedMenu(advancedMenu);
        SharedDataSingleton.getInstance().setAdvancedMode(advancedMenu);
        advancedModePropertyChangeListener = new PropertyChangeListener<Boolean>() {
            @Override
            public void onPropertyChange(String propertyName, Boolean oldValue, Boolean newValue) {
                setAdvancedMenu(newValue);
            }
        };
        SharedDataSingleton.getInstance().addAdvancedModeChangeListener(advancedModePropertyChangeListener);
        navigationView.setOnItemSelectedListener(item -> {
            Optional<MyMenuItem> optionalMyMenuItem = menuItems.stream().filter(e -> e.id == item.getItemId()).findFirst();
            if(optionalMyMenuItem.isPresent()) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.carduino_main_view, (Fragment) optionalMyMenuItem.get().clazz.newInstance()).addToBackStack(null).commit();
                    return true;
                } catch (Exception e) {
                    LoggerUtilities.logException(e);
                    return false;
                }
            } else {
                return super.onOptionsItemSelected(item);
            }
        });

        Thread.UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                LoggerUtilities.logException(new Exception(e));
            } catch(Exception ex) {
                ex.printStackTrace();
            } finally {
                if (oldHandler != null)
                    oldHandler.uncaughtException(t, e);
                else
                    System.exit(1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedDataSingleton.getInstance().removeAdvancedModeChangeListener(advancedModePropertyChangeListener);
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

    public void setAdvancedMenu(Boolean advancedMode) {
//        List<MyMenuItem> menuItemsFilteredList = menuItems.stream().filter(e -> advancedMode || (!advancedMode && !e.advancedMode)).collect(Collectors.toList());
//        navigationView.getMenu().clear();
//        for(int i = 0; i < menuItems.size(); i++) {
//            MyMenuItem e = menuItems.get(i);
//            MenuItem menuItem = navigationView.getMenu().add(e.advancedMode ? 2 : Menu.NONE, e.id, i, e.title);
//            menuItem.setIcon(e.icon);
//        }
        navigationView.getMenu().setGroupVisible(2, advancedMode);
    }

    public void stopForegroundService() {
        Intent stopIntent = new Intent(CarduinoActivity.this, ArduinoService.class);
        stopIntent.setAction("STOP_FOREGROUND");
        startService(stopIntent);
    }
}