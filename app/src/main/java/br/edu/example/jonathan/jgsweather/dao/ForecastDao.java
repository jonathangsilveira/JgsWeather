package br.edu.example.jonathan.jgsweather.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Query;

import java.util.List;

import br.edu.example.jonathan.jgsweather.model.Forecast;

@Dao
public interface ForecastDao extends BaseDao<Forecast> {

    @Query("SELECT * FROM forecast WHERE city_id = :cityId")
    List<Forecast> queryByCity(long cityId);

    @Query("DELETE FROM forecast WHERE city_id = :cityId")
    int deleteByCity(long cityId);

}
