package com.example.carduino.arduinolistener;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.utilities.ArduinoServiceUtilities;
import com.example.carduino.shared.utilities.LoggerUtilities;

public class UsbEventReceiverActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        Intent intent = getIntent();

        LoggerUtilities.logMessage("UsbEventReceiverActivity", "received event " + intent.getAction());

        if (intent != null)
        {
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                if(!ArduinoServiceUtilities.foregroundServiceRunning(getApplicationContext())) {
                    ArduinoServiceUtilities.startArduinoService(getApplicationContext());
                } else {
                    if(ArduinoSingleton.getInstance().getArduinoService() != null && !ArduinoSingleton.getInstance().getArduinoService().isConnected()) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (device != null) {
                            ArduinoSingleton.getInstance().getArduinoService().attemptConnect(device.getDeviceId(), false);
                        }
                    }
                }
            }
        }

        // Close the activity
        finish();
    }
}
