package com.example.carduino.shared;

import java.nio.ByteBuffer;

public enum PayloadType {
    UINT8(Integer.class, 1),    // Gestisce 0-255 salvandolo in un Integer Java
    BOOLEAN(Boolean.class, 1),
    UINT16(Integer.class, 2),   // Gestisce 0-65535 salvandolo in un Integer Java
    FLOAT(Float.class, 4);

    private final Class<?> javaType;
    private final int byteSize;

    PayloadType(Class<?> javaType, int byteSize) {
        this.javaType = javaType;
        this.byteSize = byteSize;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public int getByteSize() {
        return byteSize;
    }

    // Parsing da buffer binario (es. Bluetooth)
    public Object parse(ByteBuffer buffer) {
        if (buffer.remaining() < byteSize) {
            throw new IllegalArgumentException("Dati insufficienti nel buffer per " + this.name());
        }
        switch (this) {
            case UINT8:
                return buffer.get() & 0xFF; // Rimuove il segno del byte Java
            case BOOLEAN:
                return buffer.get() != 0;
            case UINT16:
                return buffer.getShort() & 0xFFFF; // Rimuove il segno dello short Java
            case FLOAT:
                return buffer.getFloat();
            default:
                throw new UnsupportedOperationException("Tipo non supportato");
        }
    }

    // Parsing da stringa seriale (es. "SWC_PAIR;2;1000;")
    public Object parseFromString(String str) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Stringa vuota non valida per il tipo " + this.name());
        }
        switch (this) {
            case UINT8:
            case UINT16:
                return Integer.parseInt(str.trim());
            case BOOLEAN:
                String clean = str.trim();
                if ("1".equals(clean)) return true;
                if ("0".equals(clean)) return false;
                return Boolean.parseBoolean(clean);
            case FLOAT:
                return Float.parseFloat(str.trim());
            default:
                throw new UnsupportedOperationException("Tipo non supportato");
        }
    }
}