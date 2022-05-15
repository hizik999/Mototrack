package com.example.project_map_curr_location.rest;

import com.example.project_map_curr_location.domain.Moto1;

public interface MotoApi {

    void fillMoto();

    void addMoto(Moto1 moto);

    void getId();

    void updateMoto(
            long id,
            long idUser,
            int speed,
            float latitude,
            float longitude,
            float altitude
    );

    void getNameByMoto(long id);

    void deleteMoto(long id);
}
