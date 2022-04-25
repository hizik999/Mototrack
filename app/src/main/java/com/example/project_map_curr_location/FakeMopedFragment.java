package com.example.project_map_curr_location;

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


public class FakeMopedFragment extends Fragment {

    private TextView tv_fakeMotos, tv_fakeMotosDesc;
    private AppCompatButton btn_goToSettings;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fake_moped, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = view.getContext();

        tv_fakeMotos = view.findViewById(R.id.tv_fakeMotos);
        tv_fakeMotosDesc = view.findViewById(R.id.tv_fakeMotosDesc);
        btn_goToSettings = view.findViewById(R.id.btn_goToSettings);

        tv_fakeMotos.setText(getText(R.string.fakeMotos));
        tv_fakeMotosDesc.setText(getText(R.string.fakeMotosDesc));

        btn_goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).loadSettings();
            }
        });
    }
}