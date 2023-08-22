package com.example.backgroundbrightness.homepage.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.backgroundbrightness.R;
import com.example.backgroundbrightness.canbus.fragments.Canbus;
import com.example.backgroundbrightness.homepage.adapters.HomepageGridButtonAdapter;
import com.example.backgroundbrightness.homepage.models.HomepageGridButtonModel;

import java.util.ArrayList;

public class Homepage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gridView = view.findViewById(R.id.gridview);
        ArrayList<HomepageGridButtonModel> courseModelArrayList = new ArrayList<>();

        courseModelArrayList.add(new HomepageGridButtonModel("CAN bus", R.drawable.ic_launcher_foreground, Canbus.class));
        courseModelArrayList.add(new HomepageGridButtonModel("K-line", R.drawable.ic_launcher_foreground, Canbus.class));
        courseModelArrayList.add(new HomepageGridButtonModel("Settings", R.drawable.ic_launcher_foreground));

        HomepageGridButtonAdapter adapter = new HomepageGridButtonAdapter(getActivity(), courseModelArrayList);
        gridView.setAdapter(adapter);
    }
}