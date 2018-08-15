package br.edu.example.jonathan.jgsweather.repository;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import br.edu.example.jonathan.jgsweather.model.City;

public class CityRepository extends OpenWeatherRepository {

    public CityRepository(Context context) {
        super(context);
    }

    public City queryByName(String name) throws IOException, JSONException, BusinessException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", name);
        return query(parameters);
    }

    public City queryById(long id) throws IOException, JSONException, BusinessException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(id));
        return query(parameters);
    }

    private City query(Map<String, String> parameters)
            throws IOException, BusinessException, JSONException {
        HttpURLConnection connection = null;
        String url = "http://api.openweathermap.org/data/2.5/weather?";
        try {
            connection = connect(url, parameters);
            if (connection == null) {
                throw new BusinessException("App couldn't connect to OpenWeather!");
            }
            String json = extractJsonFromStream(connection.getInputStream());
            return convertJsonToCity(json);
        } finally {
            disconnect(connection);
        }
    }

    City convertJsonToCity(String json) throws JSONException, BusinessException {
        JSONObject jsonObject = new JSONObject(json);
        String elementCod = "cod";
        int cod = jsonObject.getInt(elementCod);
        boolean isError = cod != 200;
        if (isError) {
            String elementMessage = "message";
            String message = jsonObject.getString(elementMessage);
            boolean notFound = cod == 404;
            if (notFound) {
                throw new CityNotFoundException(message);
            } else {
                throw new BusinessException(message);
            }
        }
        String elementId = "id";
        String elementName = "name";
        String elementSys = "sys";
        String elementSysCountry = "country";
        long id = jsonObject.getLong(elementId);
        String name = jsonObject.getString(elementName);
        JSONObject sysObject = jsonObject.getJSONObject(elementSys);
        String country = sysObject.getString(elementSysCountry);
        City city = new City();
        city.setId(id);
        city.setName(name);
        city.setCountry(country);
        return city;
    }

}
