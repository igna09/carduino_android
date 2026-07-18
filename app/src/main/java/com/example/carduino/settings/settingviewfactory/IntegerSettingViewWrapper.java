package com.example.carduino.settings.settingviewfactory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.carduino.R;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.shared.singletons.ContextsSingleton;

public class IntegerSettingViewWrapper extends SettingViewWrapper<Integer> {

    @Override
    public void generateView(Setting setting, String label) {
        View view = LayoutInflater.from(ContextsSingleton.getInstance().getApplicationContext().getForegroundActivity()).inflate(R.layout.integer_setting, null);

        if(setting != null) {
            EditText editText = view.findViewById(R.id.integer_setting_input);

            Runnable saveValue = () -> {
                try {
                    Integer value = Integer.valueOf(editText.getText().toString());
                    setting.setValue(value);
                    onAction(value);
                } catch (NumberFormatException e) {
                    // Gestione input non valido
                }
            };

            // UNICO DEPUTATO AL SALVATAGGIO: scatta sia se clicchi fuori,
            // sia quando forziamo il clearFocus() dalla tastiera
            editText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    saveValue.run();
                }
            });

            // GESTISCE SOLO IL FLUSSO UI: toglie il focus e chiude la tastiera
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Chiudendo il focus qui, l'OnFocusChangeListener sopra intercetta
                    // la perdita di focus e avvia il salvataggio una sola volta.
                    editText.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            });
        }

        TextView integerSettingLabel = view.findViewById(R.id.integer_setting_label);
        integerSettingLabel.setText(label);

        setView(view);
    }

    @Override
    public void updateView(Integer value) {
        EditText integerSettingInput = this.getView().findViewById(R.id.integer_setting_input);
        integerSettingInput.setText(value != null ? value.toString() : "");
    }
}
