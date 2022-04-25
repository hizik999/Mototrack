package com.example.project_map_curr_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int request_Code = 101;
    private List<Motorcycle> motorcycleList = new ArrayList<>();
    private boolean animateCamera = false;
    private int position;
    private int id;
    int pos;


    public MapsFragment(List<Motorcycle> motorcycleList) {
        this.motorcycleList = motorcycleList;
    }

//    public MapsFragment(int position) {
//        this.position = position;
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Toast.makeText(getContext(), "View Created", Toast.LENGTH_SHORT).show();

        RVMotosAdapter rvMotosAdapter = new RVMotosAdapter(motorcycleList);

        Bundle arguments = getArguments();





        try {
            pos = arguments.getInt("tag");

            Toast.makeText(view.getContext(), String.valueOf(pos), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(view.getContext(), "aaaaaa", Toast.LENGTH_SHORT).show();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {

            if (animateCamera) {
                mapFragment.getMapAsync(secondCallback);
            } else {
                mapFragment.getMapAsync(firstCallback);
                animateCamera = true;
            }

        }

    }


    private OnMapReadyCallback secondCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_Code);
                return;
            }

            googleMap.setMyLocationEnabled(true);

            LatLng latLng = new LatLng(motorcycleList.get(pos).getLongitude(), motorcycleList.get(pos).getLatitude());
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLng);
//            markerOptions.title(motorcycleList.get(position).getName());
//            googleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_moped)));


            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    };

    private OnMapReadyCallback firstCallback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_Code);
                return;
            }

            googleMap.setMyLocationEnabled(true);

//            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    markerOptions.position(latLng);
//                    markerOptions.title(latLng.latitude + " : " + latLng.longitude);
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                            latLng, 15
//                    ));
//                    googleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_moped)));
//                }
//            });

//            while ()


            for (int i = 0; i < motorcycleList.size(); i++) {
                LatLng latLng = new LatLng(motorcycleList.get(i).getLongitude(), motorcycleList.get(i).getLatitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(motorcycleList.get(i).getName());
                googleMap.addMarker(markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_red_moped)));

            }
        }

//            LatLng latLng = new LatLng(motorcycleList.get((int) id).getLon(), motorcycleList.get((int) id).getLat());
//            Toast.makeText(getContext(), motorcycleList.get((int) id).getName(), Toast.LENGTH_SHORT).show();
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

    };

    @NonNull
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
};
