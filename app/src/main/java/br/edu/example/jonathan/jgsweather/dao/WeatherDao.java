package br.edu.example.jonathan.jgsweather.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;

@Dao
public interface WeatherDao extends BaseDao<Weather> {

    @Query("SELECT * FROM weather WHERE city_id = :cityId")
    Weather queryByCity(long cityId);

    @Query("SELECT city.id FROM city")
    List<Long> queryCitiesId();

    @Query("DELETE FROM weather")
    int deleteAll();

    @Query("SELECT * FROM weather")
    List<Weather> queryAll();

}
