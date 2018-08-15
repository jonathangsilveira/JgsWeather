package br.edu.example.jonathan.jgsweather.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.example.jonathan.jgsweather.model.Weather;

public class WeatherRepository extends OpenWeatherRepository {

    private static final String COMMA = ",";

    WeatherRepository(Context context) {
        super(context);
    }

    @NonNull
    public List<Weather> queryByCities(List<Long> citiesId)
            throws BusinessException, IOException, JSONException {
        String url = "http://samples.openweathermap.org/data/2.5/group?";
        StringBuilder ids = new StringBuilder();
        for (long id : citiesId) {
            if (ids.length() > 0) {
                ids.append(COMMA);
            }
            ids.append(id);
        }
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", ids.toString());
        parameters.put("units", "metric");
        return query(url, parameters);
    }

    @NonNull
    private List<Weather> query(String url, Map<String, String> parameters)
            throws JSONException, BusinessException, IOException {
        HttpURLConnection connection = null;
        try {
            connection = connect(url, parameters);
            if (connection == null) {
                throw new BusinessException("App couldn't connect to OpenWeather!");
            }
            String json = extractJsonFromStream(connection.getInputStream());
            return convertJsonToWeathers(json);
        } finally {
            disconnect(connection);
        }
    }

    @NonNull
    private List<Weather> queryForecast(String url, Map<String, String> parameters)
            throws JSONException, BusinessException, IOException, ParseException {
        HttpURLConnection connection = null;
        try {
            connection = connect(url, parameters);
            if (connection == null) {
                throw new BusinessException("App couldn't connect to OpenWeather!");
            }
            String json = extractJsonFromStream(connection.getInputStream());
            return convertJsonToForecast(json);
        } finally {
            disconnect(connection);
        }
    }

    @NonNull
    List<Weather> convertJsonToWeathers(String json) throws JSONException, BusinessException {
        String elementCod = "cod";
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has(elementCod)) {
            int cod = jsonObject.getInt(elementCod);
            boolean isError = cod != 200;
            if (isError) {
                String elementMessage = "message";
                String message = jsonObject.getString(elementMessage);
                throw new BusinessException(message);
            }
        }
        String elementCnt = "cnt";
        String elementList = "list";
        String elementSys = "sys";
        String elementWeather = "weather";
        String elementMain = "main";
        String elementDt = "dt";
        String elementId = "id";
        String elementName = "name";
        String elementCountry = "country";
        String elementWeatherId = "id";
        String elementWeatherMain = "main";
        String elementWeatherDescription = "description";
        String elementMainTemp = "temp";
        String elementMainTempMin = "temp_min";
        String elementMainTempMax = "temp_max";
        List<Weather> weathers = new ArrayList<>();
        int cnt = jsonObject.getInt(elementCnt);
        if (cnt > 0) {
            JSONArray listArray = jsonObject.getJSONArray(elementList);
            for (int i = 0; i < cnt; i++) {
                JSONObject itemObject = listArray.getJSONObject(i);
                JSONObject sysObject = itemObject.getJSONObject(elementSys);
                String country = sysObject.getString(elementCountry);
                JSONArray weatherArray = itemObject.getJSONArray(elementWeather);
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                long weatherId = weatherObject.getLong(elementWeatherId);
                String weatherDescription = weatherObject.getString(elementWeatherDescription);
                String weatherMain = weatherObject.getString(elementWeatherMain);
                JSONObject mainObject = itemObject.getJSONObject(elementMain);
                double temp = mainObject.getDouble(elementMainTemp);
                double tempMin = mainObject.getDouble(elementMainTempMin);
                double tempMax = mainObject.getDouble(elementMainTempMax);
                long timestamp = itemObject.getLong(elementDt);
                long cityId = itemObject.getLong(elementId);
                String cityName = itemObject.getString(elementName);
                Weather weather = new Weather();
                weather.setId(weatherId);
                weather.setCityId(cityId);
                weather.setCityName(cityName);
                weather.setCountry(country);
                weather.setTemperature(temp);
                weather.setTemperatureMin(tempMin);
                weather.setTemperatureMax(tempMax);
                weather.setCurrentDate(new Date(timestamp));
                weather.setMain(weatherMain);
                weather.setDescription(weatherDescription);
                weathers.add(weather);
            }
        }
        return weathers;
    }

    @NonNull
    List<Weather> convertJsonToForecast(String json) throws JSONException, BusinessException, ParseException {
        String elementCod = "cod";
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has(elementCod)) {
            int cod = jsonObject.getInt(elementCod);
            boolean isError = cod != 200;
            if (isError) {
                String elementMessage = "message";
                String message = jsonObject.getString(elementMessage);
                throw new BusinessException(message);
            }
        }
        String elementCnt = "cnt";
        String elementList = "list";
        String elementWeather = "weather";
        String elementMain = "main";
        String elementDtTxt = "dt_txt";
        String elementCity = "city";
        String elementCityId = "id";
        String elementCityName = "name";
        String elementCityCountry = "country";
        String elementWeatherId = "id";
        String elementWeatherMain = "main";
        String elementWeatherDescription = "description";
        String elementMainTemp = "temp";
        String elementMainTempMin = "temp_min";
        String elementMainTempMax = "temp_max";
        List<Weather> weathers = new ArrayList<>();
        int cnt = jsonObject.getInt(elementCnt);
        JSONObject cityObject = jsonObject.getJSONObject(elementCity);
        long cityId = cityObject.getLong(elementCityId);
        String cityName = cityObject.getString(elementCityName);
        String country = cityObject.getString(elementCityCountry);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (cnt > 0) {
            JSONArray listArray = jsonObject.getJSONArray(elementList);
            for (int i = 0; i < cnt; i++) {
                JSONObject itemObject = listArray.getJSONObject(i);
                JSONArray weatherArray = itemObject.getJSONArray(elementWeather);
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                long weatherId = weatherObject.getLong(elementWeatherId);
                String weatherDescription = weatherObject.getString(elementWeatherDescription);
                String weatherMain = weatherObject.getString(elementWeatherMain);
                JSONObject mainObject = itemObject.getJSONObject(elementMain);
                double temp = mainObject.getDouble(elementMainTemp);
                double tempMin = mainObject.getDouble(elementMainTempMin);
                double tempMax = mainObject.getDouble(elementMainTempMax);
                String dateText = itemObject.getString(elementDtTxt);
                Weather weather = new Weather();
                weather.setId(weatherId);
                weather.setCityId(cityId);
                weather.setCityName(cityName);
                weather.setCountry(country);
                weather.setTemperature(temp);
                weather.setTemperatureMin(tempMin);
                weather.setTemperatureMax(tempMax);
                Date currentDate = dateFormat.parse(dateText);
                weather.setCurrentDate(currentDate);
                weather.setMain(weatherMain);
                weather.setDescription(weatherDescription);
                weathers.add(weather);
            }
        }
        return weathers;
    }

    @NonNull
    public List<Weather> queryForecastByCity(long cityId)
            throws BusinessException, IOException, JSONException, ParseException {
        String url = "http://api.openweathermap.org/data/2.5/forecast?";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("cnt", String.valueOf(5));
        parameters.put("id", String.valueOf(cityId));
        return queryForecast(url, parameters);
    }

}
