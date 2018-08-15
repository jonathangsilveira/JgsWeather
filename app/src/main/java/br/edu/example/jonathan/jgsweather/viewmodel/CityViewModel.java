package br.edu.example.jonathan.jgsweather.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.dao.CityDao;
import br.edu.example.jonathan.jgsweather.model.City;
import br.edu.example.jonathan.jgsweather.repository.BusinessException;
import br.edu.example.jonathan.jgsweather.repository.CityRepository;
import br.edu.example.jonathan.jgsweather.view.CitiesAdapter;

public class CityViewModel extends AppViewModel {

    private CityRepository mRepository;

    private CityDao mDao;

    private MutableLiveData<List<City>> mLiveCities;

    private CitiesAdapter mAdapter;

    public CityViewModel(@NonNull Application application) {
        super(application);
        mDao = getAppDatabase().cityDao();
        mRepository = new CityRepository(application.getApplicationContext());
    }

    public LiveData<List<City>> getCities() {
        if (mLiveCities == null) {
            mLiveCities = new MutableLiveData<>();
            refresh();
        }
        return mLiveCities;
    }

    public void addCity(String name) {
        AsyncAddCityTask task = new AsyncAddCityTask();
        setAsyncTask(task);
        task.execute(name);
    }

    public CitiesAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new CitiesAdapter(new ArrayList<City>(), new OnAdapterInteract());
        }
        return mAdapter;
    }

    private void deleteCity(City city) {
        AsyncDeleteCityTask task = new AsyncDeleteCityTask();
        setAsyncTask(task);
        task.execute(city);
    }

    private void refresh() {
        AsyncQueryTask task = new AsyncQueryTask();
        setAsyncTask(task);
        task.execute();
    }

    private class OnAdapterInteract implements CitiesAdapter.AdapterInteractionListener {

        @Override
        public void onDelete(int position) {
            City city = mAdapter.get(position);
            deleteCity(city);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncAddCityTask extends AsyncTask<String, Void, City> {

        private int mMessageType;

        private String mExceptionMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(City city) {
            super.onPostExecute(city);
            mProcessing.setValue(false);
            if (isError()) {
                mErrorMessage.setValue(mExceptionMessage);
            } else if (isWarning()) {
                String message = getString(R.string.device_not_connected_to_networkd);
                mMessage.setValue(message);
            } else if (city == null) {
                mMessage.setValue(getString(R.string.city_already_exists_in_database));
            } else {
                mAdapter.add(city);
                mMessage.setValue(getString(R.string.city_added));
            }
        }

        @Override
        protected City doInBackground(String... citiesName) {
            try {
                if (isConnectedToNetwork()) {
                    City city = mRepository.queryByName(citiesName[0]);
                    boolean exists = mDao.exists(city.getId());
                    if (exists) {
                        return null;
                    }
                    mDao.insert(city);
                    return city;
                } else {
                    mMessageType = 1;
                }
            } catch (IOException | JSONException e) {
                mMessageType = 2;
                mExceptionMessage =
                        String.format("Error reading response from Open Weather API.\nCause: %s",
                                e.getMessage());
            } catch (BusinessException e) {
                mMessageType = 3;
                mExceptionMessage = e.getMessage();
            }
            return null;
        }

        private boolean isWarning() {
            return mMessageType == 1;
        }

        private boolean isError() {
            return mMessageType == 2;
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncDeleteCityTask extends AsyncTask<City, Void, City> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(City city) {
            super.onPostExecute(city);
            mProcessing.setValue(false);
            mAdapter.remove(city);
            mMessage.setValue(getString(R.string.city_deleted));
        }

        @Override
        protected City doInBackground(City... cities) {
            City city = cities[0];
            mDao.delete(city);
            return city;
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncQueryTask extends AsyncTask<Void, Void, List<City>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProcessing.setValue(true);
        }

        @Override
        protected void onPostExecute(List<City> cities) {
            super.onPostExecute(cities);
            mProcessing.setValue(false);
            if (cities.isEmpty()) {
                String message = getString(R.string.query_with_no_result);
                mMessage.setValue(message);
            } else {
                mAdapter.addAll(cities);
            }
        }

        @Override
        protected List<City> doInBackground(Void... voids) {
            return mDao.queryAll();
        }

    }

}
