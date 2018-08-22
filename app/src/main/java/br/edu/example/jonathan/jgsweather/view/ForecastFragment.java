package br.edu.example.jonathan.jgsweather.view;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.viewmodel.WeatherViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {

    public static final String TAG = "ForecastFragment";

    private static final String CITY_ID = "cityId";

    private static final String CITY_NAME = "cityName";

    private long mCityId;

    private RecyclerView mRecyclerViewForecast;

    private WeatherViewModel mViewModel;

    private TextView mTextViewCity;

    private Button mButtonBack;

    private String mCityName;

    private ForecastAdapter mAdapter;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cityId city ID.
     * @return A new instance of fragment ForecastFragment.
     */
    public static ForecastFragment newInstance(long cityId, String cityName) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putLong(CITY_ID, cityId);
        args.putString(CITY_NAME, cityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null) {
            mCityId = args.getLong(CITY_ID, 0);
            mCityName = args.getString(CITY_NAME, "");
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            mViewModel = ViewModelProviders.of(activity).get(WeatherViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_forecast, container, false);
        initReferences(layout);
        setupRecyclerView();
        return layout;
    }

    private void initListeners() {
        mViewModel.getForecast().observe(this, new ForecastObserver());
        mButtonBack.setOnClickListener(new OnBackClicked());
        mViewModel.loadForecast(mCityId);
    }

    private void initReferences(View layout) {
        mRecyclerViewForecast = layout.findViewById(R.id.fragment_forecast_list);
        mTextViewCity = layout.findViewById(R.id.fragment_forecast_city);
        mButtonBack = layout.findViewById(R.id.fragment_forecast_back);
    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
        mTextViewCity.setText(mCityName);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_forecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresc) {
            mViewModel.refreshForecast();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            LinearLayoutManager linearLayoutManager =
                    new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            mAdapter = new ForecastAdapter();
            mRecyclerViewForecast.setLayoutManager(linearLayoutManager);
            mRecyclerViewForecast.setAdapter(mAdapter);
        }
    }

    private class OnBackClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.onBackPressed();
            }
        }

    }

    private class ForecastObserver implements Observer<List<Forecast>> {

        @Override
        public void onChanged(@Nullable List<Forecast> data) {
            boolean nonNull = data != null && !data.isEmpty();
            if (nonNull) {
                mAdapter.clear();
                mAdapter.addAll(data);
            }
        }

    }

}
