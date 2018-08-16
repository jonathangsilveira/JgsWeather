package br.edu.example.jonathan.jgsweather.repository;

import android.content.Context;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;

@RunWith(AndroidJUnit4.class)
public class WeatherRepository_convertJson {

    private Context mTargetContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void convertJsonToWeathers_success() throws IOException, JSONException, BusinessException {
        InputStream inputStream = mTargetContext.getResources().openRawResource(R.raw.cities);
        WeatherRepository repository = new WeatherRepository(mTargetContext);
        int expectedCount = 3;

        String json = repository.extractJsonFromStream(inputStream);
        List<Weather> weathers = repository.convertJsonToWeathers(json);

        Assert.assertEquals(expectedCount, weathers.size());
    }

    @Test
    public void forecastBlumenau_success()
            throws IOException, JSONException, BusinessException, ParseException {
        Resources resources = mTargetContext.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.forecast_blumenau_city);
        WeatherRepository repository = new WeatherRepository(mTargetContext);
        int expectedCount = 5;

        String json = repository.extractJsonFromStream(inputStream);
        List<Forecast> weathers = repository.convertJsonToForecast(json);

        Assert.assertEquals(expectedCount, weathers.size());
    }

}
