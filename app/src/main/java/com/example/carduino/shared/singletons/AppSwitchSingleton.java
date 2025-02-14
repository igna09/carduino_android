package com.example.carduino.shared.singletons;

import android.content.Intent;

import com.example.carduino.shared.models.AppToOpen;

import java.util.ArrayList;

public class AppSwitchSingleton {
    private static AppSwitchSingleton instance;

    private Integer nextPackageIndexToOpen;

    private AppSwitchSingleton(){
        nextPackageIndexToOpen = 0;
    }

    public static AppSwitchSingleton getInstance()
    {
        if (instance == null)
        {
            instance = new AppSwitchSingleton();
        }
        return instance;
    }

    public void openNextApplication() {
        AppToOpen nextAppToOpen = AppToOpen.values()[nextPackageIndexToOpen % AppToOpen.values().length];
        Intent launchIntent = ContextsSingleton.getInstance().getApplicationContext().getPackageManager().getLaunchIntentForPackage(nextAppToOpen.getPackageName());
        if (launchIntent != null) {
            ContextsSingleton.getInstance().getApplicationContext().startActivity(launchIntent);//null pointer check in case package name was not found
        }
        nextPackageIndexToOpen++;
    }

    public static void invalidate() {
        instance = null;
    }
}
