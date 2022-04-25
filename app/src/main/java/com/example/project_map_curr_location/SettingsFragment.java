package com.example.project_map_curr_location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class SettingsFragment extends Fragment {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FASTEST_UPDATE_INTERVAL = 5;
    private static final int PERMISSONS_FINE_LOCATION = 100;
    private static final String CHANNEL_ID = "123";
    private int lastSelectedId;
    private boolean switchNotificationIsChecked;
    private boolean switchMotoIsChecked;


    private TextView tv_lat, tv_labellat, tv_labellon, tv_lon,
            tv_labelaltitude, tv_altitude, tv_labelaccuracy, tv_accuracy,
            tv_labelspeed, tv_speed, tv_labelsensor, tv_sensor,
            tv_labelupdates, tv_updates, tv_address, tv_lbladdress;
    private AppCompatSpinner spinner_choice;
    private Switch sw_locationsupdates, sw_gps;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findXml(view);

        Context context = sw_locationsupdates.getContext();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.array_switch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_choice.setAdapter(adapter);



        spinner_choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                lastSelectedId = selectedItemPosition;
                //Toast.makeText(getContext(), String.valueOf(lastSelectedId), Toast.LENGTH_SHORT).show();
                ((MainActivity) context).saveDataInt("car_or_moto", lastSelectedId);

                if (((MainActivity) context).loadDataBoolean("notification_status")) {
                    ((MainActivity) context).cancelNotification(notificationManager, 1);
                    ((MainActivity) context).sendNotificationStatus();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinner_choice.setSelection(((MainActivity) context).loadDataInt("car_or_moto"));

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIVAluses(locationResult.getLastLocation());
            }
        };


        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensor");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });


        sw_locationsupdates.setChecked(((MainActivity) context).loadDataBoolean("notification_status"));
        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (sw_locationsupdates.isChecked()) {
                    switchNotificationIsChecked = true;
                    ((MainActivity) context).saveDataBoolean("notification_status", true);
                    ((MainActivity) context).cancelNotification(notificationManager, 1);
                    ((MainActivity) context).sendNotificationStatus();
                    try {
                        startLocationUpdates();
                    } catch (Exception exception) {
                        Toast.makeText(getContext(), "шото пошло не так, кеквейт", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    switchNotificationIsChecked = false;
                    ((MainActivity) context).saveDataBoolean("notification_status", false);
                    stopLocationUpdates();
                    ((MainActivity) context).cancelNotification(notificationManager, 1);
                }
            }
        });

        sw_gps.setChecked(((MainActivity) context).loadDataBoolean("show_moto"));
        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    switchMotoIsChecked = true;
                    ((MainActivity) context).saveDataBoolean("show_moto", true);

                } else {
                    switchMotoIsChecked = false;
                    ((MainActivity) context).saveDataBoolean("show_moto", false);
                }

                ((MainActivity) context).playSound();

            }
        });

        updateGPS();
        stopLocationUpdatesViews();
    }

    private void stopLocationUpdates() {
        stopLocationUpdatesViews();
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void stopLocationUpdatesViews() {
        tv_updates.setText("Location is not being tracked");
        tv_lat.setText("No tracking");
        tv_lon.setText("No tracking");
        tv_speed.setText("No tracking");
        tv_address.setText("No tracking");
        tv_accuracy.setText("No tracking");
        tv_altitude.setText("No tracking");
        tv_sensor.setText("No tracking");
    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(getContext(), "no GPS!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
        }

    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIVAluses(location);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIVAluses(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not avaliable");
        }
        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Not avaliable");
        }
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e) {
            tv_address.setText("Can't get address");
        }
    }

    private void findXml(View view) {
        tv_lat = view.findViewById(R.id.tv_lat);
        tv_labellat = view.findViewById(R.id.tv_labellat);
        tv_labellon = view.findViewById(R.id.tv_labellon);
        tv_lon = view.findViewById(R.id.tv_lon);
        tv_labelaltitude = view.findViewById(R.id.tv_labelaltitude);
        tv_altitude = view.findViewById(R.id.tv_altitude);
        tv_labelaccuracy = view.findViewById(R.id.tv_labelaccuracy);
        tv_accuracy = view.findViewById(R.id.tv_accuracy);
        tv_labelspeed = view.findViewById(R.id.tv_labelspeed);
        tv_speed = view.findViewById(R.id.tv_speed);
        tv_labelsensor = view.findViewById(R.id.tv_labelsensor);
        tv_sensor = view.findViewById(R.id.tv_sensor);
        tv_labelupdates = view.findViewById(R.id.tv_labelupdates);
        tv_updates = view.findViewById(R.id.tv_updates);
        sw_locationsupdates = view.findViewById(R.id.sw_locationsupdates);
        sw_gps = view.findViewById(R.id.sw_gps);
        tv_address = view.findViewById(R.id.tv_address);
        tv_lbladdress = view.findViewById(R.id.tv_lbladdress);
        spinner_choice = view.findViewById(R.id.spinner_choice);
    }

    public void playSound() {

    }
}