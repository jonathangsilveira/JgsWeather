package br.edu.example.jonathan.jgsweather.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import br.edu.example.jonathan.jgsweather.database.AppDatabase;
import br.edu.example.jonathan.jgsweather.database.AppDatabaseUtils;

public class AppViewModel extends AndroidViewModel {

    private AppDatabase mAppDatabase;

    MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();

    MutableLiveData<String> mMessage = new MutableLiveData<>();

    MutableLiveData<String> mErrorMessage = new MutableLiveData<>();

    private AsyncTask mTask;

    AppViewModel(@NonNull Application application) {
        super(application);
        init(application);
    }

    private void init(@NonNull Application application) {
        mAppDatabase = AppDatabaseUtils.getInstance(application.getApplicationContext());
    }

    public LiveData<Boolean> getProcessing() {
        return mProcessing;
    }

    public LiveData<String> getMessage() {
        return mMessage;
    }

    public LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    AppDatabase getAppDatabase() {
        return mAppDatabase;
    }

    protected boolean hasAnyTaskRunning() {
        return mTask != null &&  !mTask.isCancelled();
    }

    void setAsyncTask(AsyncTask task) {
        mTask = task;
    }

    String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    boolean isConnectedToNetwork() {
        Context context = getApplication().getApplicationContext();
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

}
