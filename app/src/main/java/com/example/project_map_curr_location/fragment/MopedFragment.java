package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.adapter.RVMotosAdapter;
import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.domain.Moto1;

import java.util.List;

public class MopedFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_moped, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMotos = view.findViewById(R.id.rvMotos);
        Context context = view.getContext();
        TextView tv_motos = view.findViewById(R.id.tv_motos);

        if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) != 1) {
            rvMotos.setLayoutManager(new LinearLayoutManager(getContext()));

            DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
            List<Moto1> moto;
            moto = dataBaseHelper.getAllMoto();
            Log.d("MOPEDS", moto.toString());

            String text = getString(R.string.motos_count) + ": " + moto.size();
            tv_motos.setText(text);

            RVMotosAdapter adapter = new RVMotosAdapter(view.getContext(), moto);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvMotos.setLayoutManager(layoutManager);
            rvMotos.setItemAnimator(new DefaultItemAnimator());
            rvMotos.setAdapter(adapter);



        }

    }

}