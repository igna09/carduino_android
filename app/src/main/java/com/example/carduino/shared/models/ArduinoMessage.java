package com.example.carduino.shared.models;

import android.content.Intent;

import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.settings.settingfactory.BooleanSetting;
import com.example.carduino.settings.settingfactory.IntegerSetting;
import com.example.carduino.shared.PayloadType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArduinoMessage {
    private Event event;
    private List<Object> values;

    // Costruttore completo
    public ArduinoMessage(Event event, List<Object> values) {
        this.event = event;
        this.values = values != null ? values : new ArrayList<>();
        validateValues();
    }

    // Costruttore rapido con parametri variabili (Varargs)
    public ArduinoMessage(Event event, Object... values) {
        this(event, Arrays.asList(values));
    }

    /**
     * Costruttore per il parsing diretto della stringa seriale ricevuta.
     * Accetta stringhe nel formato: "NOME_EVENTO;valore1;valore2;..."
     */
    public ArduinoMessage(String serialString) {
        if (serialString == null || serialString.trim().isEmpty()) {
            throw new IllegalArgumentException("Stringa seriale vuota o nulla");
        }

        // Dividiamo la stringa usando ";"
        String[] tokens = serialString.split(";");
        if (tokens.length == 0) {
            throw new IllegalArgumentException("Formato stringa non valido");
        }

        String firstToken = tokens[0].trim();

        // Proviamo a capire se è un ID (decimale o esadecimale) o un Nome testuale
        Integer id = tryParseId(firstToken);
        if (id != null) {
            this.event = Event.getEnumById(id);
        } else {
            this.event = Event.getEnumByName(firstToken);
        }

        if (this.event == null) {
            throw new IllegalArgumentException("Evento sconosciuto: " + firstToken);
        }

        this.values = new ArrayList<>();
        List<PayloadType> expectedTypes = this.event.getPayloadTypes();

        // Controlliamo che ci siano abbastanza parametri nella stringa
        if (tokens.length - 1 < expectedTypes.size()) {
            throw new IllegalArgumentException("Parametri insufficienti per " + this.event.name() +
                    ". Attesi: " + expectedTypes.size() + ", Ricevuti: " + (tokens.length - 1));
        }

        // Convertiamo ogni parametro stringa nel suo tipo nativo corretto
        for (int i = 0; i < expectedTypes.size(); i++) {
            String rawValue = tokens[i + 1].trim();

            // Intercettiamo i messaggi di configurazione al secondo parametro del payload (indice i == 1, ovvero tokens[2])
            if (isSettingPayloadEvent(this.event) && i == 1) {
                try {
                    // Il primo parametro del payload (tokens[1]) è l'ID del setting.
                    // Usiamo un parse protetto in grado di leggere anche eventuali formati esadecimali (es. 0x02)
                    String settingToken = tokens[1].trim();
                    Integer settingId = settingToken.startsWith("0x") || settingToken.startsWith("0X")
                            ? Integer.parseInt(settingToken.substring(2), 16)
                            : Integer.parseInt(settingToken);

                    SettingsEnum setting = (SettingsEnum) SettingsEnum.getEnumById(settingId);

                    if (setting != null) {
                        Class<?> targetClass = setting.getSettingValueType();

                        if (targetClass == BooleanSetting.class) {
                            this.values.add(parseFlexibleBoolean(rawValue));
                            continue; // Passa al prossimo token saltando il parsing standard
                        } else if (targetClass == IntegerSetting.class) {
                            this.values.add(parseFlexibleInteger(rawValue));
                            continue; // Passa al prossimo token saltando il parsing standard
                        }
                    }
                } catch (Exception e) {
                    // In caso di errore imprevisto, facciamo fallback sul parsing standard di PayloadType
                }
            }

            // Parsing standard basato sulla configurazione iniziale dell'evento
            this.values.add(expectedTypes.get(i).parseFromString(rawValue));
        }
    }

    private boolean isSettingPayloadEvent(Event event) {
        if (event == null) return false;
        String name = event.name();
        //return name.equals("READ_SETTING") || name.equals("WRITE_SETTING") || name.equals("READ_MESSAGE");
        return name.equals("READ_SETTING");
    }

    /**
     * Parsa in modo sicuro stringhe tipo "1.0", "1", "true", "0.0", "0", "false"
     */
    private boolean parseFlexibleBoolean(String val) {
        String clean = val.trim().toLowerCase();
        if ("true".equals(clean) || "1".equals(clean) || "1.0".equals(clean)) {
            return true;
        }
        if ("false".equals(clean) || "0".equals(clean) || "0.0".equals(clean)) {
            return false;
        }
        // Fallback estremo se l'hardware manda un float strano (es. "2.5")
        try {
            return Float.parseFloat(clean) != 0.0f;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parsa in modo sicuro stringhe tipo "213", "213.0", o booleani convertiti in int
     */
    private int parseFlexibleInteger(String val) {
        String clean = val.trim().toLowerCase();
        if ("true".equals(clean)) return 1;
        if ("false".equals(clean)) return 0;

        // Float.parseFloat gestisce nativamente sia "213" che "213.0" senza lanciare eccezioni
        return (int) Float.parseFloat(clean);
    }

    /**
     * Supporta sia numeri decimali (es. "12") che esadecimali (es. "0x0C") inviati da Arduino
     */
    private Integer tryParseId(String token) {
        try {
            // Gestisce eventuale formato esadecimale (es. "0x0A" o "0X0A")
            if (token.toLowerCase().startsWith("0x")) {
                return Integer.parseInt(token.substring(2), 16);
            }
            // Gestisce il classico formato decimale (es. "10")
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            // Se non è un numero, restituisce null senza rompere il flusso
            return null;
        }
    }

    // Costruttore da Android Intent
    public ArduinoMessage(Intent intent) {
        String eventStr = intent.getStringExtra("event");
        this.event = eventStr != null ? Event.getEnumByName(eventStr) : null;
        this.values = new ArrayList<>();

        if (this.event != null) {
            String[] rawValues = intent.getStringArrayExtra("values");
            if (rawValues != null) {
                List<PayloadType> expectedTypes = this.event.getPayloadTypes();
                for (int i = 0; i < Math.min(rawValues.length, expectedTypes.size()); i++) {
                    this.values.add(expectedTypes.get(i).parseFromString(rawValues[i]));
                }
            }
        }
    }

    /**
     * Scrive i dati dentro un Intent per trasmetterli all'interno del sistema Android.
     */
    public void writeToIntent(Intent intent) {
        if (event != null) {
            intent.putExtra("event", event.name());
            String[] stringValues = new String[values.size()];
            for (int i = 0; i < values.size(); i++) {
                stringValues[i] = String.valueOf(values.get(i));
            }
            intent.putExtra("values", stringValues);
        }
    }

    /**
     * Valida la congruenza tra i tipi inseriti e quelli dichiarati nell'evento.
     */
    private void validateValues() {
        if (event == null) return;

        List<PayloadType> expectedTypes = event.getPayloadTypes();
        if (values.size() != expectedTypes.size()) {
            throw new IllegalArgumentException("Numero di parametri errato per " + event.name() +
                    ". Attesi: " + expectedTypes.size() + ", Ricevuti: " + values.size());
        }

        for (int i = 0; i < values.size(); i++) {
            Class<?> expectedClass = expectedTypes.get(i).getJavaType();
            Class<?> actualClass = values.get(i).getClass();

            if (!expectedClass.isAssignableFrom(actualClass)) {
                throw new IllegalArgumentException("Tipo errato all'indice " + i + " per l'evento " + event.name() +
                        ". Atteso: " + expectedClass.getSimpleName() + ", Ricevuto: " + actualClass.getSimpleName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueAt(int index) {
        if (index < 0 || index >= values.size()) {
            return null;
        }
        return (T) values.get(index);
    }

    public Event getEvent() { return event; }
    public void setEvent(Event event) {
        this.event = event;
        validateValues();
    }

    public List<Object> getValues() { return values; }
    public void setValues(List<Object> values) {
        this.values = values;
        validateValues();
    }

    /**
     * Genera la stringa formattata per la seriale.
     * Esempio: "READ_SETTING;12;2500;"
     */
    /**
     * Genera la stringa formattata per la seriale.
     * Esempio con ID (Consigliato): "15;2;1000;" (SWC_PAIR con ID 15)
     * Esempio con Nome: "SWC_PAIR;2;1000;"
     */
    public String toSerialString() {
        if (event == null) return "";

        StringBuilder builder = new StringBuilder();

        // OPZIONE A: Invia l'ID numerico (Scelta consigliata per prestazioni su Arduino)
        builder.append(event.getId()).append(";");

        // OPZIONE B: Se preferisci il testo per debug, decommenta questa riga e commenta quella sopra:
        // builder.append(event.name()).append(";");

        for (Object val : values) {
            if (val instanceof Boolean) {
                // Se è un booleano, scrive "1" se è vero, "0" se è falso
                builder.append((Boolean) val ? "1" : "0").append(";");
            } else {
                // Per qualsiasi altro tipo di dato, si comporta come prima
                builder.append(val).append(";");
            }
        }
        return builder.toString();
    }

    public String toHumanString() {
        if (event == null) return "";

        StringBuilder builder = new StringBuilder();

        // OPZIONE A: Invia l'ID numerico (Scelta consigliata per prestazioni su Arduino)
        builder.append(event.name()).append(";");

        // OPZIONE B: Se preferisci il testo per debug, decommenta questa riga e commenta quella sopra:
        // builder.append(event.name()).append(";");

        for (Object val : values) {
            builder.append(val).append(";");
        }
        return builder.toString();
    }

    public boolean getAsBool(int index) {
        return ((Float) this.values.get(index)) != 0.0f;
    }

    public int getAsInt(int index) {
        return ((Float) this.values.get(index)).intValue();
    }

    public int getAsUint8(int index) {
        return ((Float) this.values.get(index)).intValue() & 0xFF;
    }

    public float getAsFloat(int index) {
        return (Float) this.values.get(index);
    }
}