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
import com.example.project_map_curr_location.rest.MotoApiVolley;

import java.util.List;

public class RVMotosAdapter extends RecyclerView.Adapter<RVMotosAdapter.RVMotosHolder> {

    private Context context;
    private List<Moto1> motorcycleList;
    private LayoutInflater inflater;
//
//    private DataBaseHelper dataBaseHelper;
//    private SQLiteDatabase db;
//    private Cursor cursor;
//    private SimpleCursorAdapter userAdapter;

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
            //userShowOnMap = itemView.findViewById(R.id.userShowOnMap);
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

        new MotoApiVolley(context).getNameByMoto(moto.getId());
        holder.userName.setText(((MainActivity) context).loadDataString("lastMotoName"));

        new MotoApiVolley(context).getNameByMoto(moto.getId());
        holder.userName.setText(((MainActivity) context).loadDataString("lastMotoName"));

        holder.userSpeed.setText(String.valueOf(moto.getSpeed()) + " км/ч");

        //new MotoApiVolley(context).getDistanceByMotoId(1);

        float distance = (float) Math.acos(Math.sin(moto.getLatitude()) * Math.sin(((MainActivity) context).loadDataFloat("actualCameraPositionLat"))
                + Math.cos(moto.getLatitude()) * Math.cos(((MainActivity) context).loadDataFloat("actualCameraPositionLat"))
                * Math.cos(moto.getLongitude() - Math.cos(((MainActivity) context).loadDataFloat("actualCameraPositionLon"))));


        holder.userDistance.setText(String.valueOf((int)
                calculateDistance(moto.getLatitude(), moto.getLongitude(),
                ((MainActivity) context).loadDataFloat("actualCameraPositionLat"),
                ((MainActivity) context).loadDataFloat("actualCameraPositionLon")))
        + " км");
        //((RVMotosHolder) holder).userDistance.setText((int) moto.getId());


//        String name = motorcycleList.get(position).getUser().getName();
//        holder.userName.setText(name);

//        String distance = motorcycleList.get(position)
//        holder.userDistance.setText();

//        holder.userShowOnMap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((MainActivity) context).saveDataBoolean("map_animation", true);
////                Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
//                ((MainActivity) context).loadMapInt(position);
//
//
//            }
//        });
    }

    private double calculateDistance(double Alat, double Alon, float Blat, float Blon) {

        final long EARTH_RADIUS = 6371;
        double lat1 = Alat * Math.PI / 180;
        double lat2 = Blat * Math.PI / 180;
        double lon1 = Alon * Math.PI / 180;
        double lon2 = Blon * Math.PI / 180;

        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);

        double delta = lon1 - lon2;

        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        double ad = Math.atan2(y, x);

        return ad * EARTH_RADIUS;
    }

}
