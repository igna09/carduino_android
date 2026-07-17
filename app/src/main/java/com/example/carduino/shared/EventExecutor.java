package com.example.carduino.shared;

import com.example.carduino.shared.models.Event;

public interface EventExecutor {
    void execute(Event event, Object... payloads);
}
