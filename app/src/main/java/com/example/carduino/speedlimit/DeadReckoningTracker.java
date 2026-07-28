package com.example.carduino.speedlimit;

import android.location.Location;

public class DeadReckoningTracker {
    private double lastLat, lastLon, lastHeadingDeg;
    private long lastFixTimestampMs;
    private boolean hasValidFix = false;

    private static final long GPS_TIMEOUT_MS = 3000; // se non arriva un fix da 3s, consideralo perso

    /** Chiamalo ad ogni fix GPS valido. */
    public void onGpsFix(Location loc, double headingDeg) {
        lastLat = loc.getLatitude();
        lastLon = loc.getLongitude();
        lastHeadingDeg = headingDeg;
        lastFixTimestampMs = System.currentTimeMillis();
        hasValidFix = true;
    }

    /** Chiamalo periodicamente (es. ogni 200ms) con la velocità corrente dal CAN/K-Line. */
    public double[] estimatePosition(double vehicleSpeedKmh) {
        long now = System.currentTimeMillis();
        long elapsedMs = now - lastFixTimestampMs;

        if (!hasValidFix) return null;

        // Propaga la posizione stimata in base a velocità e heading noti,
        // SENZA aggiornare lastLat/lastLon "ufficiali" (restano l'ultimo fix reale)
        double speedMs = vehicleSpeedKmh / 3.6;
        double distanceM = speedMs * (elapsedMs / 1000.0);

        double headingRad = Math.toRadians(lastHeadingDeg);
        double mLat = 111320.0;
        double mLon = 111320.0 * Math.cos(Math.toRadians(lastLat));

        double dLat = (distanceM * Math.cos(headingRad)) / mLat;
        double dLon = (distanceM * Math.sin(headingRad)) / mLon;

        return new double[]{lastLat + dLat, lastLon + dLon};
    }

    public boolean isGpsStale() {
        return (System.currentTimeMillis() - lastFixTimestampMs) > GPS_TIMEOUT_MS;
    }
}
