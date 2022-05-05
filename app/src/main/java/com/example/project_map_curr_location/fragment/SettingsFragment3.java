package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;


public class SettingsFragment3 extends Fragment {

    private ThemedToggleButtonGroup toggleGroupStatus, toggleGroupVoice, toggleGroupNotifications;
    private ThemedButton btnCar, btnMoto, btnVoiceOn, btnVoiceOff, btnNotificationOn, btnNotificationOff;
    private Context context;
    private AppCompatButton btn_startTrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());


        toggleGroupStatus = getView().findViewById(R.id.toggleGroupStatus);
        toggleGroupVoice = getView().findViewById(R.id.toggleGroupVoice);
        toggleGroupNotifications = getView().findViewById(R.id.toggleGroupNotifications);

        btnCar = getView().findViewById(R.id.btnCar);
        btnMoto = getView().findViewById(R.id.btnMoto);
        btnVoiceOn = getView().findViewById(R.id.btnVoiceOn);
        btnVoiceOff = getView().findViewById(R.id.btnVoiceOff);
        btnNotificationOn = getView().findViewById(R.id.btnNotificationOn);
        btnNotificationOff = getView().findViewById(R.id.btnNotificationOff);

        btn_startTrip = getView().findViewById(R.id.btn_startTrip);

        if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
            btn_startTrip.setText(R.string.cancelTrip);
        }


        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }

            }
        });

        btnMoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnVoiceOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnVoiceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnNotificationOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnNotificationOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btn_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))){
                    Toast.makeText(context, "Вы отменили текущий маршрут", Toast.LENGTH_SHORT).show();
                    ((MainActivity) context).saveDataBoolean(getString(R.string.tripStatus), false);
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                    ((MainActivity) context).cancelNotification(notificationManager, 1);
                    ((MainActivity) context).playSoundEnd();
                    ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 1);
                    ((MainActivity) context).cancelTripEditText();

                } else {
                    if ((btnCar.isSelected() || btnMoto.isSelected())
                            && (btnVoiceOn.isSelected() || btnVoiceOff.isSelected())
                            && (btnNotificationOn.isSelected() || btnNotificationOff.isSelected())
                            && !((MainActivity) context).loadDataString(getString(R.string.findLocationEditText)).equals("")) {

                        if (btnCar.isSelected()){
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 0);
                        }

                        if (btnMoto.isSelected()){
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 1);
                        }

                        if (btnVoiceOn.isSelected()){
                            ((MainActivity) context).saveDataBoolean(getString(R.string.voiceOn), true);
                            ((MainActivity) context).playSoundStart();
                        } else {
                            ((MainActivity) context).saveDataBoolean(getString(R.string.voiceOn), false);
                        }

                        if (btnNotificationOff.isSelected()) {
                            ((MainActivity) context).saveDataBoolean(getString(R.string.notification_status), false);
                            ((MainActivity) context).cancelNotification(notificationManager, 1);
                        } else {
                            ((MainActivity) context).saveDataBoolean(getString(R.string.notification_status), true);
                            ((MainActivity) context).sendNotificationStatus();
                        }

                        ((MainActivity) context).loadMapForTrip();
                        ((MainActivity) context).saveDataBoolean(getString(R.string.tripStatus), true);

                    } else {
                        btn_startTrip.setText(getText(R.string.startTripFail));
                        ((MainActivity) context).saveDataBoolean(getString(R.string.tripStatus), false);
                    }
                }


            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings3, container, false);
    }
}