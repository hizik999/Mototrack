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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MopedFragment extends Fragment {

    private RecyclerView rvMotos;
    private ArrayList<Motorcycle> motorcycleArrayList;
    private AppCompatButton userShowOnMap;
    private Context context;
    private TextView tv_motos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moped, null, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMotos = view.findViewById(R.id.rvMotos);
        context = view.getContext();
        tv_motos = view.findViewById(R.id.tv_motos);
        if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) == 1) {
            tv_motos.setText("Не понял");
        } else {
            rvMotos.setLayoutManager(new LinearLayoutManager(getContext()));
            setMotorcycleArrayList();
            RVMotosAdapter adapter = new RVMotosAdapter(motorcycleArrayList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvMotos.setLayoutManager(layoutManager);
            rvMotos.setItemAnimator(new DefaultItemAnimator());
            rvMotos.setAdapter(adapter);


            tv_motos.setText(getString(R.string.motos_count) + ":" + " " + motorcycleArrayList.size());
        }

    }

    private void setMotorcycleArrayList() {
        motorcycleArrayList = new ArrayList<>();

        motorcycleArrayList.add(new Motorcycle(0, 120, 37.2849947, 55.6692509, 0, "Andy"));
        motorcycleArrayList.add(new Motorcycle(1, 100, 55.6692569, 37.2849319, 0, "Senya"));
        motorcycleArrayList.add(new Motorcycle(2, 80, 55.6692579, 37.2849319, 0, "Denis"));
    }
}