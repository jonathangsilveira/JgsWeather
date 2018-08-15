package br.edu.example.jonathan.jgsweather.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import java.util.List;

import br.edu.example.jonathan.jgsweather.model.City;
import br.edu.example.jonathan.jgsweather.utils.Converters;

@Dao
@TypeConverters(Converters.class)
public interface CityDao extends BaseDao<City> {

    @Query("SELECT * FROM city")
    List<City> queryAll();

    @Query("SELECT * FROM city WHERE id = :id")
    City queryById(long id);

    @Query("SELECT 1 FROM city WHERE id = :id")
    boolean exists(long id);

}
