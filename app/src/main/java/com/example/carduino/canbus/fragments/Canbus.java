package com.example.carduino.canbus.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carduino.R;
import com.example.carduino.shared.models.ArduinoActions;
import com.example.carduino.shared.singletons.ArduinoSingleton;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Canbus extends Fragment {
    private TextView displayTextView;
    private EditText editText;
    private Button sendBtn;
    private PropertyChangeListener pcl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_canbus, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = getActivity().getApplicationContext();

        displayTextView = view.findViewById(R.id.diplayTextView);
        editText = view.findViewById(R.id.editText);
        sendBtn = view.findViewById(R.id.sendBtn);
        displayTextView.setMovementMethod(new ScrollingMovementMethod());
        displayTextView.setSingleLine(false);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTextString = editText.getText().toString();
                editText.getText().clear();

                Intent intent = new Intent();
                intent.setAction(context.getString(R.string.arduino_send_message));
                intent.putExtra("message", editTextString);
                context.sendBroadcast(intent);

                display(ArduinoActions.SEND + " --- " + editTextString);
            }
        });

        this.pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                display((String) evt.getNewValue());
            }
        };
        ArduinoSingleton.getInstance().getCircularArrayList().addPropertyChangeListener(pcl);
    }

    public void display(final String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Boolean shouldScroll = displayTextView.getLayout().getLineTop(displayTextView.getLineCount()) == displayTextView.getHeight();

                displayTextView.append(message);
                displayTextView.append("\n");

                if(/*shouldScroll*/true) {
                    // find the amount we need to scroll.  This works by
                    // asking the TextView's internal layout for the position
                    // of the final line and then subtracting the TextView's height
                    final int scrollAmount = displayTextView.getLayout().getLineTop(displayTextView.getLineCount()) - displayTextView.getHeight();
                    // if there is no need to scroll, scrollAmount will be <=0
                    if (scrollAmount > 0)
                        displayTextView.scrollTo(0, scrollAmount);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(pcl != null) {
            ArduinoSingleton.getInstance().getCircularArrayList().removePropertyChangeListener(pcl);
        }
    }
}
