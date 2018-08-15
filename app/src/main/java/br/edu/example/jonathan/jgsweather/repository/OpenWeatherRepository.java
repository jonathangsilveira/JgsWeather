package br.edu.example.jonathan.jgsweather.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import br.edu.example.jonathan.jgsweather.R;

class OpenWeatherRepository {

    private static final String PARAMETER_APPID = "APPID";

    private Context mContext;

    OpenWeatherRepository(Context context) {
        mContext = context;
    }

    @Nullable
    HttpURLConnection connect(String urlString, Map<String, String> parameters) throws IOException {
        Uri.Builder uriBuilder = Uri.parse(urlString).buildUpon();
        for (String key : parameters.keySet()) {
            String value = parameters.get(key);
            uriBuilder.appendQueryParameter(key, value);
        }
        if (!parameters.containsKey(PARAMETER_APPID)) {
            uriBuilder.appendQueryParameter(PARAMETER_APPID,
                    mContext.getString(R.string.open_wheather_app_id));
        }
        URL url = new URL(uriBuilder.build().toString());
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        //httpConnection.setConnectTimeout(3600);
        //httpConnection.setReadTimeout(3600);
        httpConnection.setRequestMethod("POST");
        httpConnection.connect();
        return httpConnection;
    }

    void disconnect(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    String extractJsonFromStream(InputStream inputStream) throws IOException {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return builder.toString();
    }

}
