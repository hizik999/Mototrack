package com.example.project_map_curr_location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private final String NOTIFICATION_CHANNEL_ID = "123";
    private static final int RecordAudioRequestCode = 201;
    private FragmentManager fm = getSupportFragmentManager();
    private Fragment fragment_map;
    private Fragment fragment_settings;
    private Fragment fragment_moped;
    private Fragment curr_fragment;
    private Fragment fragment_account;
    private Fragment fragment_fake_moped;

    private SpeechRecognizer speechRecognizer;
    private AppCompatEditText et_FindLocation;
    private AppCompatImageButton btn_findLocationMicro;
    private AppCompatImageButton btn_login;

    private SharedPreferences sharedPreferences;

    private MeowBottomNavigation bottomNavigation;

    private MediaPlayer sound;


    private static final int request_Code = 101;

    private ArrayList<Motorcycle> motorcycleArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMotorcycleArrayList();
        setBottomNavigation();
        RVMotosAdapter rvMotosAdapter = new RVMotosAdapter(this);

        et_FindLocation = findViewById(R.id.et_FindLocation);
        btn_findLocationMicro = findViewById(R.id.btn_findLocationMicro);


        btn_findLocationMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                speak();
            }
        });

        fragment_account = new AccountFragment();
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curr_fragment == fragment_account) {
                    curr_fragment = fragment_map;
                    loadFragment(curr_fragment);
                    bottomNavigation.show(2, true);
                } else {
                    curr_fragment = fragment_account;
                    loadFragment(curr_fragment);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataInt(getString(R.string.car_or_moto), 1);
    }

    private void speak() {
        //РУССКИЙ НЕ РАБОТАЕТ
        Locale russain = new Locale("RU");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                russain);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Куда Вам надо?");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    et_FindLocation.setText((CharSequence) result.get(0));
                }
                break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode
            );
        }
    }

    public void playSound() {
        sound = MediaPlayer.create(this, R.raw.bell);
        if (sound.isPlaying()) {
            sound.stop();
        }
        sound.setLooping(false);
        sound.start();
    }

    public void loadMapForTrip() {
        fm.beginTransaction().replace(R.id.frame_layout, curr_fragment).addToBackStack(null).commit();
        bottomNavigation.show(2, true);
    }

    public void loadMapInt(int position) {
        Bundle bundle = new Bundle();
        curr_fragment = fragment_map;
        if (loadDataBoolean("map_animation")) {
            bundle.putInt("position", position);
            curr_fragment.setArguments(bundle);
        }
        fm.beginTransaction().replace(R.id.frame_layout, curr_fragment).addToBackStack(null).commit();
        bottomNavigation.show(2, true);
    }

    private void loadApp() {
        fm.beginTransaction().replace(R.id.frame_layout, fragment_map).addToBackStack(null).commit();
        bottomNavigation.show(2, true);
    }

    public void loadFragment(Fragment fragment) {
        fm.beginTransaction().replace(R.id.frame_layout, fragment).addToBackStack(null).commit();
    }

    public void loadSettings(){
        curr_fragment = fragment_settings;
        fm.beginTransaction().replace(R.id.frame_layout, curr_fragment).addToBackStack(null).commit();
        bottomNavigation.show(1, true);
    }

    @Override
    public void onBackPressed() {
        if (curr_fragment == fragment_map) {
            finish();
        } else if (curr_fragment != fragment_map) {
            curr_fragment = fragment_map;
            loadFragment(curr_fragment);
            bottomNavigation.show(2, true);
        }
    }

    public void setBottomNavigation() {
        fragment_map = new MapsFragment2(motorcycleArrayList);
        fragment_settings = new SettingsFragment3();
        fragment_moped = new MopedFragment();
        fragment_fake_moped = new FakeMopedFragment();

        curr_fragment = fragment_map;
        loadFragment(curr_fragment);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_settings));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_map));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_moped));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

                switch (item.getId()) {
                    case 1:
                        curr_fragment = fragment_settings;
                        break;
                    case 2:
                        curr_fragment = fragment_map;
                        break;
                    case 3:
                        if (loadDataInt(getString(R.string.car_or_moto)) == 1){
                            curr_fragment = fragment_fake_moped;
                        } else {
                            curr_fragment = fragment_moped;
                        }
                        break;
                }

                loadFragment(curr_fragment);
            }
        });

        bottomNavigation.show(2, true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {

            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {

            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
    }

    public void setMotorcycleArrayList() {
        motorcycleArrayList = new ArrayList<>();

        //motorcycleArrayList.add(new Motorcycle(0,120, 55.6692280, 37.2849931, 0, "Andy"));
        motorcycleArrayList.add(new Motorcycle(0, 120, 37.2849947, 55.6692509, 0, "Andy"));
        motorcycleArrayList.add(new Motorcycle(1, 100, 55.6692569, 37.2849319, 0, "Senya"));
        motorcycleArrayList.add(new Motorcycle(2, 80, 55.6692579, 37.2849319, 0, "Denis"));
    }

    public void sendNotificationStatus() {
        if (loadDataBoolean(getString(R.string.notification_status))){
            int x = loadDataInt("car_or_moto");
            CharSequence title = getText(R.string.notification_title_car);
            CharSequence desc = getText(R.string.notification_desc_car);
            switch (x) {
                case 1:
                    desc = getText(R.string.notification_desc_moto);
                    title = getText(R.string.notification_title_moto);
                    break;
            }

            Intent intentMainActivity = new Intent(this, MainActivity.class);
            PendingIntent pIntentMainActivity = PendingIntent.getActivity(this, 0, intentMainActivity, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_settings)
                    .setContentTitle(title)
                    .setContentText(desc)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getText(R.string.notification_big_text)))
                    .setOngoing(true)
                    .setContentIntent(pIntentMainActivity)
                    .setAutoCancel(false);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, builder.build());
        }
    }

    public void cancelNotification(NotificationManagerCompat notificationManager, int id) {
        try {
            notificationManager.cancel(id);
        } catch (Exception e) {

        }

    }

    public void saveDataBoolean(String key, boolean b) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, b);
        editor.apply();
    }

    public boolean loadDataBoolean(String key) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean(key, false);
        return b;
    }

    public void saveDataInt(String key, int value) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int loadDataInt(String key) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int value = sharedPreferences.getInt(key, -1);
        return value;
    }
}