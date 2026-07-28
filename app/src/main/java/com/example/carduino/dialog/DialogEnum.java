package com.example.carduino.dialog;

import android.content.DialogInterface;

import com.example.carduino.shared.singletons.TripHistorySingleton;

public enum DialogEnum {
    /*CONTINUE_LAST_TRIP("Continue last trip", "Would you like to continue last trip?",
            (dialog, which) -> {
                try {
                    TripHistorySingleton.getInstance().restoreTrip();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TripHistorySingleton.getInstance().startTrip();
            },
            (dialog, which) -> {
                try {
                    TripHistorySingleton.getInstance().resetTrip();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TripHistorySingleton.getInstance().startTrip();
            },
            new TimedDialogAction(Duration.SHORT, (dialog, which) -> {
                try {
                    TripHistorySingleton.getInstance().restoreTrip();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                TripHistorySingleton.getInstance().startTrip();
            })),*/
    BLE_PAIRING_CODE("BLE pairing code", "code",
            null,
            null,
            null),
    SPEED_LIMIT_SET("Speed limit set", "speed",
            null,
            null,
            new TimedDialogAction(Duration.SHORT, null));

    private DialogInterface.OnClickListener positiveCallback;
    private DialogInterface.OnClickListener negativeCallback;
    private String message;
    private String title;
    private TimedDialogAction timedDialogAction;

    DialogEnum(String title, String message, DialogInterface.OnClickListener positiveCallback, DialogInterface.OnClickListener negativeCallback, TimedDialogAction timedDialogAction) {
        this.positiveCallback = positiveCallback;
        this.negativeCallback = negativeCallback;
        this.title = title;
        this.message = message;
        this.timedDialogAction = timedDialogAction;
    }

    public DialogInterface.OnClickListener getPositiveCallback() {
        return positiveCallback;
    }

    public DialogInterface.OnClickListener getNegativeCallback() {
        return negativeCallback;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public TimedDialogAction getTimedDialogAction() {
        return timedDialogAction;
    }
}
