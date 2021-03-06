package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;


public class FakeMopedFragment extends Fragment {

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fake_moped, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        TextView tv_fakeMotos = view.findViewById(R.id.tv_fakeMotos);
        TextView tv_fakeMotosDesc = view.findViewById(R.id.tv_fakeMotosDesc);
        AppCompatButton btn_goToSettings = view.findViewById(R.id.btn_goToSettings);

        tv_fakeMotos.setText(getText(R.string.fakeMotos));
        tv_fakeMotosDesc.setText(getText(R.string.fakeMotosDesc));

        btn_goToSettings.setOnClickListener(view1 -> ((MainActivity) context).loadSettings());
    }
}