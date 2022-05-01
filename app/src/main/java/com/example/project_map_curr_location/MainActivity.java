package com.example.project_map_curr_location;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.mapboxsdk.maps.MapView;

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

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FASTEST_UPDATE_INTERVAL = 5;
    private static final int PERMISSONS_FINE_LOCATION = 100;
    private boolean geoStatus = true;

    private static final int request_Code = 101;

    private ArrayList<Motorcycle> motorcycleArrayList;


    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMotorcycleArrayList();
        setBottomNavigation();
        RVMotosAdapter rvMotosAdapter = new RVMotosAdapter(this);
        micAndLogin();

        et_FindLocation = findViewById(R.id.et_FindLocation);

//        et_FindLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                //Toast.makeText(getApplicationContext(), "ААААА" + et_FindLocation.getText(), Toast.LENGTH_SHORT).show();
//                saveDataString(getString(R.string.findLocationEditText), String.valueOf(et_FindLocation.getText()));
//                return false;
//            }
//
//
//        });


        et_FindLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveDataString(getString(R.string.findLocationEditText), String.valueOf(et_FindLocation.getText()));
            }
        });

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


        Thread geoThread = new MyTread();

        geoThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataInt(getString(R.string.car_or_moto), 1);
        geoStatus = false;
    }

    private class MyTread extends Thread{

        @Override
        public void run() {
            updateGPS();
            while (geoStatus){
                updateGPS();
                try {
                    sleep(1 * 5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateUIVAluses(Location location) {
        saveDataFloat(getString(R.string.actualCameraPositionLat), (float) location.getLatitude());
        saveDataFloat(getString(R.string.actualCameraPositionLon), (float) location.getLongitude());

        Toast.makeText(getApplicationContext(), loadDataFloat(getString(R.string.actualCameraPositionLat)) + ": " + loadDataFloat(getString(R.string.actualCameraPositionLon)), Toast.LENGTH_SHORT).show();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    Toast.makeText(MainActivity.this, "no GPS!", Toast.LENGTH_SHORT).show();
//                    finish();
                }
        }

    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
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



    public void cancelTripEditText(){
        saveDataString(getString(R.string.findLocationEditText), "");
        et_FindLocation.setText("");
    }

    private void micAndLogin(){
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



    private void speak() {
        //РУССКИЙ НЕ РАБОТАЕТ
        Locale russian = new Locale("RU");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                russian);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Куда Вам надо?");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "а?шо?", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String res = result.get(0);
                    et_FindLocation.setText((CharSequence) res);
                    //Toast.makeText(getApplicationContext(), "ЗАСЕЙВИЛ" + res, Toast.LENGTH_SHORT).show();
                    saveDataString(getString(R.string.findLocationEditText), String.valueOf(res));
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

    public void playSoundStart() {
        sound = MediaPlayer.create(this, R.raw.start);
        if (sound.isPlaying()) {
            sound.stop();
        }
        sound.setLooping(false);
        sound.start();
    }

    public void playSoundEnd() {
        sound = MediaPlayer.create(this, R.raw.back);
        if (sound.isPlaying()) {
            sound.stop();
        }
        sound.setLooping(false);
        sound.start();
    }

    public void loadMapForTrip() {
        curr_fragment = fragment_map;
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
//        fragment_map = new MapsFragment2(motorcycleArrayList);
        //fragment_map = new MapsFragment2(motorcycleArrayList);
        fragment_map = new YandexMapFragment(motorcycleArrayList);
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
        motorcycleArrayList.add(new Motorcycle(0, 120, 37.2849947, 55.6692509, 0, "Мама Дениса"));
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

    public void saveDataFloat(String key, float value){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float loadDataFloat(String key) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        float value = sharedPreferences.getFloat(key, 0);
        return value;
    }

    public void saveDataString(String key, String value){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String loadDataString(String key) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "");
        return value;
    }
}