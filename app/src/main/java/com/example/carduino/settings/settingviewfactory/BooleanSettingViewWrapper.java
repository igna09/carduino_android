package com.example.carduino.settings.settingviewfactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.carduino.R;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.shared.singletons.ContextsSingleton;

public class BooleanSettingViewWrapper extends SettingViewWrapper<Boolean> {
    private CompoundButton.OnCheckedChangeListener listener;

    @Override
    public void generateView(Setting setting, String label) {
        View view = LayoutInflater.from(ContextsSingleton.getInstance().getApplicationContext().getForegroundActivity()).inflate(R.layout.boolean_setting, null);

        if(setting != null) {
            SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.boolean_setting_switch);
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (switchCompat.isPressed()) {
                    setting.setValue(isChecked);
                    onAction(isChecked);
                }
            });
        }

        TextView booleanSettingLabel = view.findViewById(R.id.boolean_setting_label);
        booleanSettingLabel.setText(label);

        setView(view);
    }

    @Override
    public void updateView(Boolean value) {
        SwitchCompat booleanSettingSwitch = this.getView().findViewById(R.id.boolean_setting_switch);
        booleanSettingSwitch.setChecked(value);
    }
}
