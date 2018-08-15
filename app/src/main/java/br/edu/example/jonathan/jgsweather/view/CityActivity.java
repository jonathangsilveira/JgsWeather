package br.edu.example.jonathan.jgsweather.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.components.DeviderItemDecoration;
import br.edu.example.jonathan.jgsweather.utils.DeviceUtils;
import br.edu.example.jonathan.jgsweather.utils.DialogUtils;
import br.edu.example.jonathan.jgsweather.viewmodel.CityViewModel;

public class CityActivity extends AppCompatActivity {

    private CityViewModel mViewModel;

    private EditText mEditTextName;

    private Button mButtonAdd;

    private RecyclerView mRecyclerViewCities;

    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        initViewModel();
        initReferences();
        initListeners();
        setupRecyclerView();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(CityViewModel.class);
        mViewModel.getCities();
        mViewModel.getErrorMessage().observe(this, new ErrorObserver());
        mViewModel.getMessage().observe(this, new MessageObserver());
        mViewModel.getProcessing().observe(this, new ProcessingObserver());
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerViewCities.setLayoutManager(linearLayoutManager);
        int marginHorizontal = (int) getResources().getDimension(R.dimen.activity_margin_vertical);
        DeviderItemDecoration itemDecoration =
                new DeviderItemDecoration(this, LinearLayoutManager.VERTICAL, marginHorizontal);
        mRecyclerViewCities.addItemDecoration(itemDecoration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRecyclerViewCities.setAdapter(mViewModel.getAdapter());
    }

    private void initListeners() {
        mEditTextName.addTextChangedListener(new NameTextWatcher());
        mButtonAdd.setOnClickListener(new OnAddClicked());
    }

    private void initReferences() {
        mEditTextName = findViewById(R.id.activity_city_name);
        mButtonAdd = findViewById(R.id.activity_city_add);
        mRecyclerViewCities = findViewById(R.id.activity_city_cities);
        mProgressBar = findViewById(R.id.activity_city_progress);
    }

    private void showSimpleMessage(String message) {
        View view = findViewById(R.id.activity_city);
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }

    private class OnAddClicked implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CityActivity activity = CityActivity.this;
            DeviceUtils.hideKeyboard(activity);
            String name = mEditTextName.getText().toString();
            mViewModel.addCity(name);
            mEditTextName.getText().clear();
        }

    }

    private class NameTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mButtonAdd.setEnabled(s.length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

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
            DialogUtils.showErrorDialog(CityActivity.this, message);
        }

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

}