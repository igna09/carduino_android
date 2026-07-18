package com.example.carduino.shared.utilities;

import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.singletons.ArduinoSingleton;

import java.util.Arrays;

public class ArduinoMessageUtilities {
    private static String[] parseMessage(String message, String delimeter) {
        String[] values = message.split(delimeter, -1);
        values = Arrays.stream(values).filter(s -> !s.isEmpty()).toArray(String[]::new);
        return values;
    }

    public static String[] parseArduinoMessage(String message) {
        return parseMessage(message, ";");
    }

    public static void sendArduinoMessage(ArduinoMessage message) {
        if (message == null || message.getEvent() == null) {
            LoggerUtilities.logArduinoMessage("ArduinoService", "Tentativo di invio di un messaggio nullo o non valido");
            return;
        }

        LoggerUtilities.logArduinoMessage("ArduinoService", "sending " + message.toHumanString());

        // Invio effettivo tramite il servizio seriale/Bluetooth
        ArduinoSingleton.getInstance().getArduinoService().sendMessageToArduino(message.toSerialString());
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
