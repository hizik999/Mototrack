package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.database.DataBaseHelper;
import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.User;
import com.example.project_map_curr_location.rest.MotoApiVolley;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;


public class SettingsFragment3 extends Fragment {

    private ThemedToggleButtonGroup toggleGroupStatus, toggleGroupVoice, toggleGroupNotifications;
    private ThemedButton btnCar, btnMoto, btnVoiceOn, btnVoiceOff, btnNotificationOn, btnNotificationOff;
    private Context context;
    private AppCompatButton btn_startTrip;
    private AppCompatEditText et_name;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

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

        et_name = getView().findViewById(R.id.et_Name);
        et_name.setHint("Введите свой никнейм");
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((MainActivity) context).saveDataString(getString(R.string.userNickname), String.valueOf(et_name.getText()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((MainActivity) context).saveDataString(getString(R.string.userNickname), String.valueOf(et_name.getText()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

                ((MainActivity) context).saveDataString(getString(R.string.userNickname), String.valueOf(et_name.getText()));

            }
        });

//        et_FindLocation.setText(loadDataString(getString(R.string.findLocationEditText)));


        btn_startTrip = getView().findViewById(R.id.btn_startTrip);

        if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
            btn_startTrip.setText(R.string.cancelTrip);
        }


        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }

            }
        });

        btnMoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnVoiceOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnVoiceOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnNotificationOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btnNotificationOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                }
            }
        });

        btn_startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((MainActivity) context).loadDataBoolean(getString(R.string.tripStatus))) {
                    Toast.makeText(getContext(), String.valueOf(((MainActivity) context).loadDataInt(getString(R.string.motoId))), Toast.LENGTH_SHORT).show();
                    new MotoApiVolley(getContext()).deleteMoto(((MainActivity) context).loadDataInt(getString(R.string.motoId)));
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

                        if (btnCar.isSelected()) {
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 0);
                        }

                        if (btnMoto.isSelected()) {
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 1);

//                            Toast.makeText(getContext(), ((MainActivity) context).loadDataInt("userId"), Toast.LENGTH_SHORT).show();
//                            try {
//
//                            } catch (Exception e){
//                                e.printStackTrace();
//                            }

                            if (((MainActivity) context).loadDataInt(getString(R.string.motoId)) == -1) {
                                User user = new User(((MainActivity) context).loadDataInt("userId"), "", ((MainActivity) context).loadDataString(getString(R.string.userNickname)), "", "moto");

                                Moto1 moto = new Moto1(((MainActivity) context).loadDataInt(getString(R.string.motoId)), user, ((MainActivity) context).loadDataInt(getString(R.string.actualSpeed)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon)),
                                        0);

                                new MotoApiVolley(getContext()).addMoto(moto);

                            } else {
                                //new MotoApiVolley(getContext()).updateMoto();
                            }


                        }

                        if (btnVoiceOn.isSelected()) {
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