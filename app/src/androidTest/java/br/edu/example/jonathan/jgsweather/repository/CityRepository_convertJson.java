package br.edu.example.jonathan.jgsweather.repository;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.City;

@RunWith(AndroidJUnit4.class)
public class CityRepository_convertJson {

    private Context mTargetContext = InstrumentationRegistry.getTargetContext();

    @Test(expected = CityNotFoundException.class)
    public void convertJsonToCity_cityNotFound()
            throws IOException, JSONException, BusinessException {
        InputStream inputStream = getInputStream(R.raw.city_not_found);
        CityRepository repository = new CityRepository(mTargetContext);
        String json = repository.extractJsonFromStream(inputStream);
        repository.convertJsonToCity(json);
    }

    @Test
    public void convertJsonToCity_success() throws IOException, JSONException, BusinessException {
        InputStream inputStream = getInputStream(R.raw.blumenau_city);
        CityRepository repository = new CityRepository(mTargetContext);
        int expectedId = 3469968;

        String json = repository.extractJsonFromStream(inputStream);
        City city = repository.convertJsonToCity(json);

        Assert.assertEquals(expectedId, city.getId());
    }

    private InputStream getInputStream(@RawRes int resId) {
        return mTargetContext.getResources().openRawResource(resId);
    }

}
