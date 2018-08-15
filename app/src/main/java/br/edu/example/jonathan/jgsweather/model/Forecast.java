package br.edu.example.jonathan.jgsweather.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import br.edu.example.jonathan.jgsweather.utils.Converters;

@Entity(tableName = "forecast", foreignKeys =
        @ForeignKey(entity = City.class, parentColumns = "id", childColumns = "city_id",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(Converters.class)
public class Forecast {

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "city_id")
    private long cityId;

    @ColumnInfo(name = "city_name")
    private String cityName;

    private String country;

    private double temperature;

    @ColumnInfo(name = "temperature_min")
    private double temperatureMin;

    @ColumnInfo(name = "temperature_max")
    private double temperatureMax;

    @ColumnInfo(name = "current_date")
    private Date currentDate;

    private String main;

    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
