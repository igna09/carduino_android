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
        ArduinoSingleton.getInstance().getArduinoService().sendMessageToArduino(stringifyArduinoMessage(message));
    }

    public static String stringifyArduinoMessage(ArduinoMessage message) {
        String builder = message.getAction().name() +
                ";" +
                message.getKey() +
                ";" +
                message.getValue() +
                ";";
        return builder;
    }
}
