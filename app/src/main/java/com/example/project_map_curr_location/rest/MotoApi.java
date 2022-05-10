package com.example.project_map_curr_location.rest;

import com.example.project_map_curr_location.domain.Moto1;

import java.util.List;

public interface MotoApi {

    void fillMoto();

    List<Moto1> getMoto();

    void addMoto(Moto1 moto);

    void updateMoto(
            long id,
            long idUser,
            int speed,
            float latitude,
            float longitude,
            float altitude
    );

    void deleteMoto(long id);
}
