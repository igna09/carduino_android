package com.example.carduino.shared.singletons;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

public class ContextsSingleton {
    private static ContextsSingleton contextsSingleton;
    private AppCompatActivity mainActivityContext;
    private Application applicationContext;

    private ContextsSingleton(){}
    public static ContextsSingleton getInstance()
    {
        if (contextsSingleton == null)
        {
            contextsSingleton = new ContextsSingleton();
        }
        return contextsSingleton;
    }

    public AppCompatActivity getMainActivityContext() {
        return mainActivityContext;
    }

    public void setMainActivityContext(AppCompatActivity mainActivityContext) {
        this.mainActivityContext = mainActivityContext;
    }

    public Application getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Application applicationContext) {
        this.applicationContext = applicationContext;
    }
}