package com.example.carduino.dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carduino.shared.utilities.LoggerUtilities;

public class DialogActivity extends AppCompatActivity {

    private AlertDialog alertDialog;
    private Thread autoCloseDialogThread;

    private String currentDialogKey;
    private String currentMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDialogFromIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showDialogFromIntent(intent);
    }

    private void showDialogFromIntent(Intent intent) {
        String newDialogKey = intent.getStringExtra("DIALOG");
        String newMessage = intent.getStringExtra("MESSAGE");

        boolean sameDialog = alertDialog != null && alertDialog.isShowing()
                && newDialogKey != null && newDialogKey.equals(currentDialogKey);

        // ferma timer precedente in ogni caso (va riavviato o sostituito)
        if (autoCloseDialogThread != null && autoCloseDialogThread.isAlive()) {
            autoCloseDialogThread.interrupt();
        }

        try {
            DialogEnum dialogEnum = DialogEnum.valueOf(newDialogKey);
            String message = newMessage != null ? newMessage : dialogEnum.getMessage();

            currentDialogKey = newDialogKey;
            currentMessage = message;

            if (sameDialog) {
                // stessa dialog già visibile: aggiorna solo testo/titolo, niente dismiss/show (no flash)
                alertDialog.setMessage(message);
                alertDialog.setTitle(dialogEnum.getTitle());
            } else {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.setOnDismissListener(null); // evita finish() indesiderato
                    alertDialog.dismiss();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(message);
                builder.setTitle(dialogEnum.getTitle());
                builder.setCancelable(dialogEnum.getPositiveCallback() == null && dialogEnum.getNegativeCallback() == null);
                if (dialogEnum.getPositiveCallback() != null) {
                    builder.setPositiveButton("Yes", dialogEnum.getPositiveCallback());
                }
                if (dialogEnum.getNegativeCallback() != null) {
                    builder.setNegativeButton("No", dialogEnum.getNegativeCallback());
                }
                builder.setOnDismissListener(dialogInterface -> finish());

                alertDialog = builder.create();
                runOnUiThread(() -> alertDialog.show());
            }

            // in entrambi i casi il timer riparte da zero
            if (dialogEnum.getTimedDialogAction() != null) {
                startAutoCloseThread(dialogEnum, alertDialog);
            }
        } catch (Exception e) {
            LoggerUtilities.logException(e);
            finish();
        }
    }

    private void startAutoCloseThread(DialogEnum dialogEnum, AlertDialog dialog) {
        autoCloseDialogThread = new Thread(() -> {
            int leftDuration = dialogEnum.getTimedDialogAction().getDuration().getDuration();
            while (leftDuration > 0 && !Thread.currentThread().isInterrupted()) {
                int finalLeftDuration = leftDuration;
                runOnUiThread(() -> {
                    if (dialog.isShowing() && dialog.getButton(DialogInterface.BUTTON_POSITIVE) != null) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(String.format("Yes (%d)", finalLeftDuration));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return; // thread cancellato per nuova dialog in arrivo
                }
                leftDuration--;
            }
            if (dialog.isShowing()) {
                if (dialogEnum.getTimedDialogAction().getOnClickListener() != null) {
                    runOnUiThread(() -> dialogEnum.getTimedDialogAction().getOnClickListener().onClick(dialog, -1));
                }
                dialog.dismiss();
            }
        });
        autoCloseDialogThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoCloseDialogThread != null) {
            autoCloseDialogThread.interrupt();
        }
    }
}