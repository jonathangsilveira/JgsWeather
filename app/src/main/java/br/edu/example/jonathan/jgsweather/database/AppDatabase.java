package br.edu.example.jonathan.jgsweather.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import br.edu.example.jonathan.jgsweather.dao.CityDao;
import br.edu.example.jonathan.jgsweather.dao.WeatherDao;
import br.edu.example.jonathan.jgsweather.model.City;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;

@Database(entities = {City.class, Weather.class, Forecast.class}, version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CityDao cityDao();

    public abstract WeatherDao weatherDao();

}
