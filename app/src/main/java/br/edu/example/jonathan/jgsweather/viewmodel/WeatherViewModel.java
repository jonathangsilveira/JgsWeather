package br.edu.example.jonathan.jgsweather.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.dao.ForecastDao;
import br.edu.example.jonathan.jgsweather.dao.WeatherDao;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;
import br.edu.example.jonathan.jgsweather.repository.WeatherRepository;
import br.edu.example.jonathan.jgsweather.utils.DateUtils;

public class WeatherViewModel extends AppViewModel {

    private String mCurrentFragmentTag;

    private long mCityId;

    private WeatherRepository mRepository;

    private WeatherDao mWeatherDao;

    private ForecastDao mForecastDao;

    private MutableLiveData<String> mLiveDay;

    private List<Weather> mWeathers = new ArrayList<>();

    private NameComparator mByName;

    private TemperatureMinCompatator mByMin;

    private TemperatureMaxCompatator mByMax;

    private MutableLiveData<List<Weather>> mLiveWeather;

    private MutableLiveData<List<Forecast>> mLiveForecast;

    WeatherViewModel(@NonNull Application application) {
        super(application);
        mWeatherDao = getAppDatabase().weatherDao();
        mForecastDao = getAppDatabase().forecastDao();
        mRepository = new WeatherRepository(application.getApplicationContext());
    }

    public void setCurrentFragmentTag(String tag) {
        mCurrentFragmentTag = tag;
    }

    public String getCurrentFragmentTag() {
        if (mCurrentFragmentTag == null) {
            mCurrentFragmentTag = "";
        }
        return mCurrentFragmentTag;
    }

    public LiveData<String> getLiveDay() {
        if (mLiveDay == null) {
            mLiveDay = new MutableLiveData<>();
        }
        return mLiveDay;
    }

    public LiveData<List<Weather>> getCurrentWeather() {
        if (mLiveWeather == null) {
            mLiveWeather = new MutableLiveData<>();
            refreshWeather();
        }
        return mLiveWeather;
    }

    public LiveData<List<Forecast>> getForecast() {
        if (mLiveForecast == null) {
            mLiveForecast = new MutableLiveData<>();
        }
        return mLiveForecast;
    }

    public void refreshForecast() {
        runForecast(mCityId);
    }

    public void loadForecast(long cityId) {
        if (cityId != mCityId) {
            mCityId = cityId;
            refreshForecast();
        }
    }

    private void runForecast(long cityId) {
        AsyncForecastTask task = new AsyncForecastTask();
        setAsyncTask(task);
        task.execute(cityId);
    }

    public void refreshWeather() {
        AsyncWeatherTask task = new AsyncWeatherTask();
        setAsyncTask(task);
        task.execute();
    }

    public void sortByName() {
        if (mByName == null) {
            mByName = new NameComparator();
        }
        Collections.sort(mWeathers, mByName);
    }

    public void sortByMin() {
        if (mByMin == null) {
            mByMin = new TemperatureMinCompatator();
        }
        Collections.sort(mWeathers, mByMin);
    }

    public void sortByMax() {
        if (mByMax == null) {
            mByMax = new TemperatureMaxCompatator();
        }
        Collections.sort(mWeathers, mByMax);

    }

    @NonNull
    private List<Weather> queryWeatherFromNetwork() throws Exception {
        List<Long> ids = mWeatherDao.queryCitiesId();
        List<Weather> weathers = new ArrayList<>(mRepository.queryByCities(ids));
        if (!weathers.isEmpty()) {
            mWeatherDao.deleteAll();
            for (Weather weather : weathers) {
                mWeatherDao.insert(weather);
            }
        }
        return weathers;
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncForecastTask extends AsyncTask<Long, Void, Result<List<Forecast>>> {

        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(Result<List<Forecast>> result) {
            super.onPostExecute(result);
            mProcessing.setValue(false);
            if (result.isError()) {
                mErrorMessage.setValue(result.getMessage());
            }
            if (result.isWarning()) {
                mMessage.setValue(result.getMessage());
            } else if (result.isSuccess()) {
                if (result.hasData()) {
                    List<Forecast> data = result.getData();
                    mLiveForecast.setValue(data);
                    if (result.isNetworkSource()) {
                        mMessage.setValue(getString(R.string.forecast_updated));
                    }
                }
            }
        }

        @Override
        protected Result<List<Forecast>> doInBackground(Long... cityIds) {
            List<Forecast> data = new ArrayList<>();
            Result<List<Forecast>> result = new Result<>();
            result.setData(data);
            Long cityId = cityIds[0];
            boolean isConnected = isConnectedToNetwork();
            if (isConnected) {
                try {
                    List<Forecast> forecast = queryForecastFromNetwork(cityId);
                    data.addAll(forecast);
                    result.setSuccess(true);
                    result.setSourceNetwork();
                    return result;
                } catch (Exception e) {
                    result.setMessageLevelError();
                    result.setMessage(e.getMessage());
                }
            }
            if (data.isEmpty()) {
                data.addAll(mForecastDao.queryByCity(cityId));
            }
            if (data.isEmpty()) {
                result.setMessageLevelWarning();
                result.setSuccess(false);
                String message;
                if (isConnected) {
                    message = getString(R.string.query_with_no_result);
                } else {
                    message = getString(R.string.device_not_connected_to_network);
                }
                result.setMessage(message);
            } else {
                result.setSuccess(true);
            }
            return result;
        }

    }

    @NonNull
    private List<Forecast> queryForecastFromNetwork(Long cityId) throws Exception {
        List<Forecast> currentForecast = mRepository.queryForecastByCity(cityId);
        if (!currentForecast.isEmpty()) {
            Log.d("WeatherViewModel", String.format("Updating city id: %d", cityId));
            List<Forecast> result = mForecastDao.queryByCity(cityId);
            Log.d("WeatherViewModel", String.format("Rows count: %d", result.size()));
            int rowsAffected = mForecastDao.deleteByCity(cityId);
            Log.d("WeatherViewModel", String.format("Rows affected: %d", rowsAffected));
            result = mForecastDao.queryByCity(cityId);
            Log.d("WeatherViewModel", String.format("Rows count now: %d", result.size()));
            for (Forecast weather : currentForecast) {
                mForecastDao.insert(weather);
            }
        }
        return currentForecast;
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncWeatherTask extends AsyncTask<Void, Void, Result<List<Weather>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(Result<List<Weather>> result) {
            super.onPostExecute(result);
            mProcessing.setValue(false);
            if (result.isError()) {
                mErrorMessage.setValue(result.getMessage());
            }
            if (result.isWarning()) {
                mMessage.setValue(result.getMessage());
            } else if (result.isSuccess()) {
                if (result.hasData()) {
                    assert result.getData() != null;
                    List<Weather> data = result.getData();
                    mWeathers.clear();
                    mWeathers.addAll(data);
                    mLiveWeather.setValue(mWeathers);
                    Weather first = data.get(0);
                    String day = DateUtils.getDay(first.getCurrentDate());
                    mLiveDay.setValue(day);
                    if (result.isNetworkSource()) {
                        mMessage.setValue(getString(R.string.current_weather_updated));
                    }
                }
            }
        }

        @Override
        protected Result<List<Weather>> doInBackground(Void... voids) {
            List<Weather> weathers = new ArrayList<>();
            boolean isConnected = isConnectedToNetwork();
            Result<List<Weather>> result = new Result<>();
            result.setData(weathers);
            if (isConnected) {
                try {
                    List<Weather> queryResult = queryWeatherFromNetwork();
                    weathers.addAll(queryResult);
                    if (!weathers.isEmpty()) {
                        result.setSourceNetwork();
                        result.setSuccess(true);
                        return result;
                    }
                } catch (Exception e) {
                    result.setMessageLevelError();
                    result.setMessage(e.getMessage());
                }
            }
            if (weathers.isEmpty()) {
                weathers.addAll(mWeatherDao.queryAll());
            }
            if (weathers.isEmpty()) {
                result.setMessageLevelWarning();
                String message;
                if (isConnected) {
                    message = getString(R.string.query_with_no_result);
                } else {
                    message = getString(R.string.device_not_connected_to_network);
                }
                result.setMessage(message);
                result.setSuccess(false);
            } else {
                result.setSuccess(true);
                result.setSourceDatabase();
            }
            return result;
        }

    }

    private class NameComparator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            String thisName = thisOne.getCityName();
            String thatName = thatOne.getCityName();
            return thisName.compareToIgnoreCase(thatName);
        }

    }

    private class TemperatureMinCompatator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            Double thisMin = thisOne.getTemperatureMin();
            Double thatMin = thatOne.getTemperatureMin();
            return thisMin.compareTo(thatMin);
        }

    }

    private class TemperatureMaxCompatator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            Double thisMax = thisOne.getTemperatureMax();
            Double thatMax = thatOne.getTemperatureMax();
            return thatMax.compareTo(thisMax);
        }

    }

}
