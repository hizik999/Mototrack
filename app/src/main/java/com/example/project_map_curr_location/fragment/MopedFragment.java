package com.example.project_map_curr_location.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_map_curr_location.DataBaseHelper;
import com.example.project_map_curr_location.MainActivity;
import com.example.project_map_curr_location.R;
import com.example.project_map_curr_location.adapter.RVMotosAdapter;
import com.example.project_map_curr_location.domain.Moto1;
import com.example.project_map_curr_location.rest.MotoApiVolley;

import java.util.ArrayList;
import java.util.List;

public class MopedFragment extends Fragment {

    private RecyclerView rvMotos;

    private AppCompatButton userShowOnMap;
    private Context context;
    private TextView tv_motos;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private SimpleCursorAdapter userAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dataBaseHelper = new DataBaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moped, null, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMotos = view.findViewById(R.id.rvMotos);
        context = view.getContext();
        tv_motos = view.findViewById(R.id.tv_motos);
        if (((MainActivity) context).loadDataInt(getString(R.string.car_or_moto)) == 1) {
            tv_motos.setText("Ты волшебник или как?");
        } else {
            rvMotos.setLayoutManager(new LinearLayoutManager(getContext()));


            dataBaseHelper = new DataBaseHelper(getContext());
            List<Moto1> moto = new ArrayList<>();


            new MotoApiVolley(getContext()).fillMoto();
            moto = dataBaseHelper.getAllMoto();


            RVMotosAdapter adapter = new RVMotosAdapter(view.getContext(), moto);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvMotos.setLayoutManager(layoutManager);
            rvMotos.setItemAnimator(new DefaultItemAnimator());
            rvMotos.setAdapter(adapter);


            tv_motos.setText(getString(R.string.motos_count) + ":" + " " + moto.size());
        }

    }

//    private List<Moto1> setMotorcycleArrayList() {
//        motorcycleArrayList = new ArrayList<>();
//        List<User> userList = new ArrayList<>();

//        db = dataBaseHelper.getReadableDatabase();
//        cursor =  db.rawQuery("SELECT * from moto", null);
//        String[] headers = new String[] {"id", "user_id", "speed", "latitude", "longitude", "altitude"};
//        // создаем адаптер, передаем в него курсор
//        userAdapter = new SimpleCursorAdapter(context, android.R.layout.two_line_list_item,
//                cursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
//        userName.setText("Найдено элементов: " +  cursor.getCount());
//        userList.setAdapter(userAdapter);



//        cursor = db.rawQuery("SELECT * FROM moto", null);


        //motorcycleArrayList = new MotoApiVolley(getContext()).getMoto();

//        motorcycleArrayList =
////        Log.d("API_TEST1", userList.toString());
////        Log.d("API_TEST1", motorcycleArrayList.toString());
//
//        return motorcycleArrayList;
//        //motorcycleArrayList.add(new Motorcycle(0,120, 55.6692280, 37.2849931, 0, "Andy"));
////        motorcycleArrayList.add(new Moto1(0, 120, 37.2849947, 55.6692509, 0, "Andy"));
////        motorcycleArrayList.add(new Moto1(1, 100, 55.6692569, 37.2849319, 0, "Senya"));
////        motorcycleArrayList.add(new Moto1(2, 80, 55.6692579, 37.2849319, 0, "Denis"));
//        //motorcycleArrayList.add(new Moto1(0, userList.get(1), 37, 55.66925, 3.4354, 21.32));
//    }

//    private List<Moto1>
}