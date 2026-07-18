package com.example.carduino.settings.settingviewfactory;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.example.carduino.R;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.shared.singletons.ContextsSingleton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class FloatSettingViewWrapper extends SettingViewWrapper<Float> {

    @Override
    public void generateView(Setting setting, String label) {
        View view = LayoutInflater.from(ContextsSingleton.getInstance().getApplicationContext().getForegroundActivity()).inflate(R.layout.float_setting, null);

        TextView floatSettingLabel = view.findViewById(R.id.float_setting_label);
        floatSettingLabel.setText(label);

        if(setting != null) {
            EditText editText = view.findViewById(R.id.float_setting_input);

            // Logica di salvataggio centralizzata
            Runnable saveValue = () -> {
                try {
                    Float value = Float.valueOf(editText.getText().toString());
                    setting.setValue(value);
                    onAction(value);
                } catch (NumberFormatException e) {
                    // Evita crash se l'input è vuoto o malformato (es. solo un punto ".")
                }
            };

            // Gestisce il salvataggio effettivo quando il focus viene perso
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    saveValue.run();
                }
            });

            // Gestisce la chiusura e la rimozione del focus alla pressione del tasto OK verde
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.clearFocus(); // Questo scatena indirettamente l'OnFocusChangeListener sopra

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            });
        }

        setView(view);
    }

    @Override
    public void updateView(Float value) {
        EditText floatSettingInput = this.getView().findViewById(R.id.float_setting_input);
        floatSettingInput.setText(value != null ? value.toString() : "");
    }
}
