package br.edu.example.jonathan.jgsweather.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.dao.ForecastDao;
import br.edu.example.jonathan.jgsweather.dao.WeatherDao;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.model.Weather;
import br.edu.example.jonathan.jgsweather.repository.BusinessException;
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

    @SuppressLint("StaticFieldLeak")
    private class AsyncForecastTask extends AsyncTask<Long, Void, List<Forecast>> {

        private String mMsg;

        private int mType = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(List<Forecast> forecasts) {
            super.onPostExecute(forecasts);
            mProcessing.setValue(false);
            if (hasError()) {
                mErrorMessage.setValue(mMsg);
            }
            if (hasWarning()) {
                mMessage.setValue(mMsg);
            } else if (!forecasts.isEmpty()) {
                mForecastAdapter.clear();
                mForecastAdapter.addAll(forecasts);
            }
        }

        @Override
        protected List<Forecast> doInBackground(Long... cityIds) {
            List<Forecast> forecast = new ArrayList<>();
            Long cityId = cityIds[0];
            boolean isConnected = isConnectedToNetwork();
            if (isConnected) {
                try {
                    List<Forecast> currentForecast = mRepository.queryForecastByCity(cityId);
                    forecast.addAll(currentForecast);
                    if (!forecast.isEmpty()) {
                        mForecastDao.deleteByCity(cityId);
                        for (Forecast weather : forecast) {
                            mForecastDao.insert(weather);
                        }
                        return forecast;
                    }
                } catch (BusinessException | IOException | JSONException e) {
                    mType = 2;
                    mMsg = e.getMessage();
                }
            }
            boolean isEmpty = mForecastAdapter.getItemCount() == 0;
            if (isEmpty) {
                if (forecast.isEmpty()) {
                    forecast.addAll(mForecastDao.queryByCity(cityId));
                }
                if (forecast.isEmpty()) {
                    mType = 1;
                    if (isConnected) {
                        mMsg = getString(R.string.query_with_no_result);
                    } else {
                        mMsg = getString(R.string.device_not_connected_to_network);
                    }
                }
            }
            return forecast;
        }

        private boolean hasError() {
            return mType == 1;
        }

        private boolean hasWarning() {
            return mType == 2;
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncWeatherTask extends AsyncTask<Void, Void, List<Weather>> {

        private String mMsg;

        private int mType;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(List<Weather> weathers) {
            super.onPostExecute(weathers);
            mProcessing.setValue(false);
            if (hasError()) {
                mErrorMessage.setValue(mMsg);
            }
            if (hasWarning()) {
                mMessage.setValue(mMsg);
            } else if (!weathers.isEmpty()) {
                mWeatherAdapter.clear();
                mWeatherAdapter.addAll(weathers);
                Weather first = weathers.get(0);
                String day = DateUtils.getDay(first.getCurrentDate());
                mLiveDay.setValue(day);
            }
        }

        @Override
        protected List<Weather> doInBackground(Void... voids) {
            List<Weather> weathers = new ArrayList<>();
            boolean isConnected = isConnectedToNetwork();
            if (isConnected) {
                List<Long> ids = mWeatherDao.queryCitiesId();
                try {
                    weathers.addAll(mRepository.queryByCities(ids));
                    if (!weathers.isEmpty()) {
                        mWeatherDao.deleteAll();
                        for (Weather weather : weathers) {
                            mWeatherDao.insert(weather);
                        }
                        return weathers;
                    }
                } catch (BusinessException | IOException | JSONException e) {
                    mType = 2;
                    mMsg = e.getMessage();
                }
            }
            boolean isEmpty = mWeatherAdapter.getItemCount() == 0;
            if (isEmpty) {
                if (weathers.isEmpty()) {
                    weathers.addAll(mWeatherDao.queryAll());
                }
                if (weathers.isEmpty()) {
                    mType = 1;
                    if (isConnected) {
                        mMsg = getString(R.string.query_with_no_result);
                    } else {
                        mMsg = getString(R.string.device_not_connected_to_network);
                    }
                }
            }
            return weathers;
        }

        private boolean hasError() {
            return mType == 2;
        }

        private boolean hasWarning() {
            return mType == 1;
        }

    }

}
