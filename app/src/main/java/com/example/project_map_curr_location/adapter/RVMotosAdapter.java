package com.example.project_map_curr_location.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.domain.Moto;
import com.example.project_map_curr_location.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RVMotosAdapter extends RecyclerView.Adapter<RVMotosAdapter.RVMotosHolder> {

    private Context context;
    private List<Moto> motorcycleList = new ArrayList<Moto>();
    private Map<Integer, String> positions = new ArrayMap<>();

    public RVMotosAdapter(List<Moto> motorcycleArrayList) {
        this.motorcycleList = motorcycleArrayList;
    }

    public RVMotosAdapter(Context context) {

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

    public void setItems(Collection<Moto> motorcycles) {
        motorcycleList.addAll(motorcycles);
        notifyDataSetChanged();
    }

    public void clearItems() {
        motorcycleList.clear();
        notifyDataSetChanged();
    }

    public void setItem(Moto motorcycle) {
        motorcycleList.add(motorcycle);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RVMotosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();


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
        String name = motorcycleList.get(position).getName();
        holder.userName.setText(name);
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
