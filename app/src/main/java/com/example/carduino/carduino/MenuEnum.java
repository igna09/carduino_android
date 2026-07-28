package com.example.carduino.carduino;

import com.example.carduino.R;
import com.example.carduino.canbus.fragments.Canbus;
import com.example.carduino.carstatus.Carstatus;
import com.example.carduino.settings.fragments.Settings;
import com.example.carduino.test.Test;
import com.example.carduino.trip.TripFragment;

public enum MenuEnum {
    CARSTATUS(0, "Carstatus", R.drawable.baseline_directions_car_24, Carstatus.class, false),
    TRIP(1, "Trip", R.drawable.baseline_bar_chart_24, TripFragment.class, false),
    SETTINGS(3, "Settings", R.drawable.baseline_settings_24, Settings.class, false),
    CANBUS(2, "Canbus", R.drawable.baseline_usb_24, Canbus.class, true),
    TEST(4, "Test", R.drawable.baseline_tips_and_updates_24, Test.class, true);

    Integer id;
    String title;
    Integer iconResource;
    Class fragmentClass;
    Boolean advancedMode;

    MenuEnum(Integer id, String title, Integer iconResource, Class fragmentClass, Boolean advancedMode) {
        this.id = id;
        this.title = title;
        this.iconResource = iconResource;
        this.fragmentClass = fragmentClass;
        this.advancedMode = advancedMode;
    }
}
