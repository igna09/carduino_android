package com.example.carduino.shared;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.Event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventDispatcher {
    // Cache per salvare le istanze degli executor specifici (es. ReadSettingExecutor)
    private final Map<Class<? extends ArduinoMessageExecutorInterface>, ArduinoMessageExecutorInterface> specificExecutorsCache = new ConcurrentHashMap<>();

    public void dispatch(ArduinoMessage message) {
        Event event = message.getEvent();
        ArduinoMessageExecutorInterface executor;

        // 1. Controlla se l'evento ha un executor dedicato (override)
        if (event.getExecutorClass() != null) {
            executor = specificExecutorsCache.computeIfAbsent(
                    event.getExecutorClass(),
                    clazz -> {
                        try {
                            // Viene eseguito SOLO la prima volta che l'evento si presenta
                            return clazz.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Impossibile istanziare l'executor specifico: " + clazz.getName(), e);
                        }
                    }
            );
        } else {
            // 2. Altrimenti usa l'executor globale della sua categoria (già istanziato)
            executor = event.getCategory().getExecutor();
        }

        // 3. Esegui la logica
        if (executor != null) {
            executor.execute(message);
        }
    }
}
