package com.example.carduino.settings.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carduino.R;
import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.settings.settingviewfactory.SettingViewFactory;
import com.example.carduino.settings.settingviewfactory.SettingViewWrapper;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.Event;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.PropertyChangeListener;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.utilities.ArduinoMessageUtilities;
import com.example.carduino.shared.utilities.LoggerUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * workflow:
 * android fragment settings asks for settings --> arduino can nodes responds for their settings --> android collects all settings in {@link com.example.carduino.shared.singletons.SharedDataSingleton} --> when opened {@link Settings} expose stored settings if any
 * automatic backlight sensor
 * Specchietto si abbassa con retro
 * Finestrino si apre con telecomando
 * Specchietti chiusura automatica (se selettore non in posizione chiusa)
 */
public class Settings extends Fragment {
    private PropertyChangeListener pcl;
    private LinearLayout settingsListLeft;
    private LinearLayout settingsListRight;
    Map<String, SettingViewWrapper> settingViews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        settingsListLeft = (LinearLayout) view.findViewById(R.id.settings_list_left);
        settingsListRight = (LinearLayout) view.findViewById(R.id.settings_list_right);

        settingViews = new HashMap<>();

        /**
         * creating view for only app settings
         */
        Arrays.stream(SettingsEnum.values())
                .filter(settingEnum -> settingEnum.getSettingType() == SettingsEnum.SettingType.APP && settingEnum.getSettingViewType() != null)
                .forEach(settingEnum -> {
                    Setting setting = SettingsSingleton.getInstance().getSettings().get(settingEnum.name());
                    if(setting != null) {
                        if(!settingViews.containsKey(settingEnum.name())) {
                            SettingViewWrapper settingViewWrapper = SettingViewFactory.getSettingView(settingEnum.name());
                            settingViewWrapper.generateView(setting, settingEnum.getLabel());
                            settingViews.put(settingEnum.name(), settingViewWrapper);
                        }
                    }
                });
        renderViews();

        pcl = new PropertyChangeListener<Setting>() {
            @Override
            public void onPropertyChange(String propertyName, Setting oldSetting, Setting newSetting) {
                if(!settingViews.containsKey(propertyName)) {
                    SettingViewWrapper settingViewWrapper = SettingViewFactory.getSettingView(propertyName);
                    settingViewWrapper.generateView(newSetting, newSetting.getLabel());
                    settingViews.put(propertyName, settingViewWrapper);
                    renderViews();
                }
            }
        };
        SettingsSingleton.getInstance().addPropertyChangeListener(pcl);

        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.GET_SETTINGS.name())); // start reading all node settings

        /*ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            ArduinoSingleton.getInstance().getArduinoService().onArduinoMessage("45;0;150;");
            ArduinoSingleton.getInstance().getArduinoService().onArduinoMessage("45;1;true;");
            ArduinoSingleton.getInstance().getArduinoService().onArduinoMessage("45;2;1;");
            ArduinoSingleton.getInstance().getArduinoService().onArduinoMessage("45;3;1;");
            scheduler.shutdown();
        };
        scheduler.schedule(task, 5, TimeUnit.SECONDS);*/
    }

    private void renderViews() {
        getActivity().runOnUiThread(() -> {
            settingsListLeft.removeAllViews();
            settingsListRight.removeAllViews();

            Integer index = 0;
            for(int i = 0; i < SettingsEnum.values().length; i++) {
                SettingsEnum settingEnum = SettingsEnum.values()[i];
                if(settingViews.containsKey(settingEnum.name())) {
                    SettingViewWrapper settingViewWrapper = settingViews.get(settingEnum.name());
                    settingViewWrapper.updateView(SettingsSingleton.getInstance().getSettings().get(settingEnum.name()).getValue());
                    ViewGroup parent = (ViewGroup) settingViewWrapper.getView().getParent();
                    if (parent != null) {
                        parent.removeView(settingViewWrapper.getView());
                    }
                    final int j = index;
                    if(j % 2 == 0) {
                        settingsListLeft.addView(settingViewWrapper.getView());
                    } else {
                        settingsListRight.addView(settingViewWrapper.getView());
                    }
                    index++;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingsSingleton.getInstance().removePropertyChangeListener(pcl);
    }
}
