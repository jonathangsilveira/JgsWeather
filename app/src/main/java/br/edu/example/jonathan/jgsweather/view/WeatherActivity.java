package br.edu.example.jonathan.jgsweather.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.utils.DialogUtils;
import br.edu.example.jonathan.jgsweather.viewmodel.WeatherViewModel;

public class WeatherActivity extends AppCompatActivity {

    private WeatherViewModel mViewModel;

    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
        initViewModel();
        mProgressBar = findViewById(R.id.activity_weather_progress);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        mViewModel.getProcessing().observe(this, new ProcessingObserver());
        mViewModel.getErrorMessage().observe(this, new ErrorObserver());
        mViewModel.getMessage().observe(this, new MessageObserver());
    }

    private void showSimpleMessage(String message) {
        View view = findViewById(R.id.activity_weather);
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String currentTag = mViewModel.getCurrentFragmentTag();
        Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
        if (currentFragment == null) {
            currentFragment = new CurrentWeatherFragment();
            currentTag = CurrentWeatherFragment.TAG;
            mViewModel.setCurrentFragmentTag(currentTag);
        }
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.activity_weather_container, currentFragment, currentTag)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initFragment();
    }

    private class ProcessingObserver implements Observer<Boolean> {

        @Override
        public void onChanged(@Nullable Boolean processing) {
            boolean isProcessing = processing != null && processing;
            if (isProcessing) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }

    }

    private class MessageObserver implements Observer<String> {

        @Override
        public void onChanged(@Nullable String message) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            showSimpleMessage(message);
        }

    }

    private class ErrorObserver implements Observer<String> {

        @Override
        public void onChanged(@Nullable String message) {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            WeatherActivity context = WeatherActivity.this;
            DialogUtils.showErrorDialog(context, message);
        }

    }

}
