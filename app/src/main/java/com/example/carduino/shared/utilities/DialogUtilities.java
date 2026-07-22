package com.example.carduino.shared.utilities;

import android.content.Intent;

import com.example.carduino.dialog.DialogActivity;
import com.example.carduino.shared.singletons.ContextsSingleton;

public class DialogUtilities {
    public static Boolean isShowingDialog() {
        return ContextsSingleton.getInstance().getApplicationContext()
                .getForegroundActivity().getLocalClassName().equals("dialog.DialogActivity");
    }

    private static void openDialogInternal(String dialogKey, String message) {
        Intent intent = new Intent(ContextsSingleton.getInstance().getApplicationContext(), DialogActivity.class);
        intent.putExtra("DIALOG", dialogKey);
        if (message != null) intent.putExtra("MESSAGE", message);

        // NEW_TASK è obbligatorio da Application Context, in ogni caso
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        ContextsSingleton.getInstance().getApplicationContext().startActivity(intent);
    }

    public static void openContinueLastTrip() {
        openDialogInternal("CONTINUE_LAST_TRIP", null);
    }

    public static void openDialogBLEPairingCode(String code) {
        openDialogInternal("BLE_PAIRING_CODE", code);
    }

    public static void openDialogSpeedLimitSet(String speed) {
        openDialogInternal("SPEED_LIMIT_SET", speed);
    }
}
