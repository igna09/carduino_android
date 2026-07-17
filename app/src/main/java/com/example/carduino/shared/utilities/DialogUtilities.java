package com.example.carduino.shared.utilities;

import android.content.Intent;

import com.example.carduino.dialog.DialogActivity;
import com.example.carduino.shared.singletons.ContextsSingleton;

public class DialogUtilities {
    public static Boolean isShowingDialog() {
        return ContextsSingleton.getInstance().getApplicationContext().getForegroundActivity().getLocalClassName().equals("dialog.DialogActivity");
    }
    public static void openContinueLastTrip() {
        Intent intent = new Intent(ContextsSingleton.getInstance().getApplicationContext(), DialogActivity.class);
        intent.putExtra("DIALOG", "CONTINUE_LAST_TRIP");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextsSingleton.getInstance().getApplicationContext().startActivity(intent);
    }
    public static void openDialogBLEPairingCode(String code) {
        Intent intent = new Intent(ContextsSingleton.getInstance().getApplicationContext(), DialogActivity.class);
        intent.putExtra("DIALOG", "BLE_PAIRING_CODE");
        intent.putExtra("MESSAGE", code);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextsSingleton.getInstance().getApplicationContext().startActivity(intent);
    }

    public static void openDialogSpeedLimitSet(String speed) {
        Intent intent = new Intent(ContextsSingleton.getInstance().getApplicationContext(), DialogActivity.class);
        intent.putExtra("DIALOG", "SPEED_LIMIT_SET");
        intent.putExtra("MESSAGE", speed);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextsSingleton.getInstance().getApplicationContext().startActivity(intent);
    }
}
