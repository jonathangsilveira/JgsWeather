package br.edu.example.jonathan.jgsweather.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.components.DeviderItemDecoration;
import br.edu.example.jonathan.jgsweather.model.Weather;
import br.edu.example.jonathan.jgsweather.viewmodel.WeatherViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {

    public static final String TAG = "CurrentWeatherFragment";

    private FloatingActionButton mFab;

    private RecyclerView mRecyclerViewCities;

    private WeatherViewModel mViewModel;

    private TextView mTextViewTitle;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_current_weather, container, false);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            mViewModel = ViewModelProviders.of(activity).get(WeatherViewModel.class);
            mViewModel.getLiveDay().observe(this, new DayObserver());
        }
        initReferences(layout);
        initListeners();
        setupRecyclerView();
        return layout;
    }

    private void initListeners() {
        mFab.setOnClickListener(new OnFabClicked());
    }

    private void initReferences(View layout) {
        mRecyclerViewCities = layout.findViewById(R.id.fragment_current_weather_cities);
        mFab = layout.findViewById(R.id.fab);
        mTextViewTitle = layout.findViewById(R.id.fragment_current_weather_title);
    }

    private void setupRecyclerView() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            mRecyclerViewCities.setLayoutManager(linearLayoutManager);
            DeviderItemDecoration itemDecoration =
                    new DeviderItemDecoration(activity, LinearLayoutManager.VERTICAL, 0);
            mRecyclerViewCities.addItemDecoration(itemDecoration);
        }
    }

    private void startCityActivity() {
        startActivity(CityActivity.newIntent(getActivity()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_current_weather, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresc:
                mViewModel.refreshWeather();
                break;
            case R.id.action_sort_by_name:
                mViewModel.sortByName();
                break;
            case R.id.action_sort_by_min:
                mViewModel.sortByMin();
                break;
            case R.id.action_sort_by_max:
                mViewModel.sortByMax();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        WeatherAdapterListener listener = new WeatherAdapterListener();
        WeatherAdapter adapter = mViewModel.getWeatherAdapter(listener);
        mRecyclerViewCities.setAdapter(adapter);
    }

    private class OnFabClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            startCityActivity();
        }

    }

    private class DayObserver implements Observer<String> {

        @Override
        public void onChanged(@Nullable String day) {
            if (TextUtils.isEmpty(day)) {
                return;
            }
            mTextViewTitle.setText(day);
        }

    }

    private class WeatherAdapterListener implements WeatherAdapter.AdapterInteractionListener {

        @Override
        public void onItemClick(Weather weather) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                Fragment forecast = ForecastFragment.newInstance(weather.getCityId());
                fragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.activity_weather_container, forecast, ForecastFragment.TAG)
                        .addToBackStack(TAG)
                        .commit();
            }
        }

    }

}
