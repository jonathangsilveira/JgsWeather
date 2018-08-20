package br.edu.example.jonathan.jgsweather.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.dao.ForecastDao;
import br.edu.example.jonathan.jgsweather.dao.WeatherDao;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;
import br.edu.example.jonathan.jgsweather.repository.WeatherRepository;
import br.edu.example.jonathan.jgsweather.utils.DateUtils;
import br.edu.example.jonathan.jgsweather.view.ForecastAdapter;
import br.edu.example.jonathan.jgsweather.view.WeatherAdapter;

public class WeatherViewModel extends AppViewModel {

    private String mCurrentFragmentTag;

    private long mCityId;

    private WeatherRepository mRepository;

    private WeatherDao mWeatherDao;

    private ForecastDao mForecastDao;

    private WeatherAdapter mWeatherAdapter;

    private ForecastAdapter mForecastAdapter;

    private MutableLiveData<String> mLiveDay;

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

    public WeatherAdapter getWeatherAdapter(WeatherAdapter.AdapterInteractionListener listener) {
        if (mWeatherAdapter == null) {
            mWeatherAdapter = new WeatherAdapter(new ArrayList<Weather>(), listener);
            refreshWeather();
        }
        return mWeatherAdapter;
    }

    public ForecastAdapter getForecastAdapter() {
        if (mForecastAdapter == null) {
            mForecastAdapter = new ForecastAdapter(new ArrayList<Forecast>());
        }
        return mForecastAdapter;
    }

    public void refreshWeather() {
        AsyncWeatherTask task = new AsyncWeatherTask();
        setAsyncTask(task);
        task.execute();
    }

    public void sortByName() {
        mWeatherAdapter.sortByName();
    }

    public void sortByMin() {
        mWeatherAdapter.sortByMin();
    }

    public void sortByMax() {
        mWeatherAdapter.sortByMax();
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
                    assert data != null;
                    mForecastAdapter.clear();
                    mForecastAdapter.addAll(data);
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
            mForecastDao.deleteByCity(cityId);
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
                    mWeatherAdapter.clear();
                    mWeatherAdapter.addAll(data);
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

}
