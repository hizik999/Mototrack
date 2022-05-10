package com.example.project_map_curr_location.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.domain.Moto1;

import java.util.List;

public class RVMotosAdapter extends RecyclerView.Adapter<RVMotosAdapter.RVMotosHolder> {

    private Context context;
    private List<Moto1> motorcycleList;
    private LayoutInflater inflater;

    public RVMotosAdapter(Context context, List<Moto1> motorcycleList) {
        this.context = context;
        this.motorcycleList = motorcycleList;
    }

    public RVMotosAdapter(List<Moto1> motorcycleArrayList) {
        this.motorcycleList = motorcycleArrayList;
    }

    public RVMotosAdapter(Context context) {
        this.context = context;
    }

    public class RVMotosHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView userDistance;
        private TextView userSpeed;
        private AppCompatButton userShowOnMap;

        public RVMotosHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            userDistance = itemView.findViewById(R.id.userDistance);
            userSpeed = itemView.findViewById(R.id.userSpeed);
            userShowOnMap = itemView.findViewById(R.id.userShowOnMap);


        }

    }

    @NonNull
    @Override
    public RVMotosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_moto, parent, false);
        return new RVMotosHolder(view);
    }

    @Override
    public int getItemCount() {
        return motorcycleList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RVMotosHolder holder, @SuppressLint("RecyclerView") int position) {

        Moto1 moto = motorcycleList.get(position);
        ((RVMotosHolder) holder).userName.setText(moto.getUser().getName());

//        String name = motorcycleList.get(position).getUser().getName();
//        holder.userName.setText(name);

//        String distance = motorcycleList.get(position)
//        holder.userDistance.setText();

        holder.userShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).saveDataBoolean("map_animation", true);
//                Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
                ((MainActivity) context).loadMapInt(position);


            }
        });
    }

}
