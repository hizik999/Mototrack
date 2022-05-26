package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.domain.User;
import com.example.project_map_curr_location.rest.MotoApiVolley;
import com.example.project_map_curr_location.rest.UserApiVolley;

import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;


public class SettingsFragment3 extends Fragment {

    private ThemedToggleButtonGroup toggleGroupStatus, toggleGroupVoice, toggleGroupNotifications;
    private ThemedButton btnCar, btnMoto, btnVoiceOn, btnVoiceOff, btnNotificationOn, btnNotificationOff;
    private Context context;
    private AppCompatButton btn_startTrip;
    private AppCompatEditText et_name;

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
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ((MainActivity) context).saveDataString(getString(R.string.userNickname), String.valueOf(et_name.getText()));

            }
        });

        et_name.setText(((MainActivity) context).loadDataString(getString(R.string.userNickname)));


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
                    new MotoApiVolley(getContext()).deleteMoto(((MainActivity) context).loadDataInt(getString(R.string.motoId)));
                    ((MainActivity) context).saveDataBoolean(getString(R.string.tripStatus), false);
                    btn_startTrip.setText(getText(R.string.startTripSuccess));
                    ((MainActivity) context).cancelNotification(notificationManager, 1);
                    ((MainActivity) context).playSoundEnd();
                    ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 0);
                    ((MainActivity) context).cancelTripEditText();
                    new UserApiVolley(getContext()).updateUser(
                            ((MainActivity) context).loadDataInt("userId"),
                            "",
                            ((MainActivity) context).loadDataString(getString(R.string.userNickname)), "", "car"
                    );
                    ((MainActivity) context).saveDataInt(getString(R.string.motoId), -1);


                } else {


                    if ((btnCar.isSelected() || btnMoto.isSelected())
                            && (btnVoiceOn.isSelected() || btnVoiceOff.isSelected())
                            && (btnNotificationOn.isSelected() || btnNotificationOff.isSelected())
                            && !((MainActivity) context).loadDataString(getString(R.string.findLocationEditText)).equals("")) {

                        if (btnCar.isSelected()) {
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 0);

                            new UserApiVolley(getContext()).updateUser(
                                    ((MainActivity) context).loadDataInt("userId"),
                                    "",
                                    ((MainActivity) context).loadDataString(getString(R.string.userNickname)), "", "car"
                            );
                        }

                        if (btnMoto.isSelected()) {
                            ((MainActivity) context).saveDataInt(getString(R.string.car_or_moto), 1);
                            new UserApiVolley(getContext()).updateUser(
                                    ((MainActivity) context).loadDataInt("userId"),
                                    "",
                                    ((MainActivity) context).loadDataString(getString(R.string.userNickname)), "", "moto"
                            );

                            if (((MainActivity) context).loadDataInt(getString(R.string.motoId)) == -1) {
                                User user = new User(((MainActivity) context).loadDataInt("userId"), "", ((MainActivity) context).loadDataString(getString(R.string.userNickname)), "", "car");

                                Moto1 moto = new Moto1(((MainActivity) context).loadDataInt(getString(R.string.motoId)), user, ((MainActivity) context).loadDataInt(getString(R.string.actualSpeed)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionAlt)));

                                new MotoApiVolley(getContext()).addMoto(moto);

                            } else {
                                new MotoApiVolley(getContext()).updateMoto(
                                        ((MainActivity) context).loadDataInt(getString(R.string.motoId)),
                                        ((MainActivity) context).loadDataInt("userId"),
                                        ((MainActivity) context).loadDataInt(getString(R.string.actualSpeed)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLat)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionLon)),
                                        ((MainActivity) context).loadDataFloat(getString(R.string.actualCameraPositionAlt)));

                            }


                        }

                        if (btnVoiceOn.isSelected()) {
                            ((MainActivity) context).saveDataBoolean(getString(R.string.voiceOn), true);
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