package br.edu.example.jonathan.jgsweather.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.utils.DateUtils;
import br.edu.example.jonathan.jgsweather.utils.OpenWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private List<Forecast> mForecast = new ArrayList<>();

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_weather, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final Forecast weather = mForecast.get(position);
        Date date = weather.getCurrentDate();
        holder.mTextViewTitle.setText(DateUtils.getDay(date));
        holder.mTextViewCondition.setText(weather.getDescription());
        DecimalFormat decimal = (DecimalFormat) NumberFormat.getInstance();
        decimal.setMinimumFractionDigits(2);
        decimal.setMaximumFractionDigits(2);
        holder.mTextViewTempMin.setText(decimal.format(weather.getTemperatureMin()));
        holder.mTextViewTempMax.setText(decimal.format(weather.getTemperatureMax()));
        holder.mTextViewTemp.setText(decimal.format(weather.getTemperature()));
        int iconResource = OpenWeatherUtils.getWeatherIcon(weather.getWeatherCode());
        holder.mImageViewIcon.setImageResource(iconResource);
    }

    public void addAll(List<Forecast> forecast) {
        mForecast.addAll(forecast);
        notifyDataSetChanged();
    }

    public void clear() {
        mForecast.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }


    class ForecastViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTitle;

        private TextView mTextViewCondition;

        private TextView mTextViewTempMin;

        private TextView mTextViewTempMax;

        private ImageView mImageViewIcon;

        private TextView mTextViewTemp;

        ForecastViewHolder(View itemView) {
            super(itemView);
            mTextViewTitle = itemView.findViewById(R.id.item_card_weather_title);
            mTextViewCondition = itemView.findViewById(R.id.item_card_weather_condition);
            mTextViewTempMin = itemView.findViewById(R.id.item_card_weather_temp_min);
            mTextViewTempMax = itemView.findViewById(R.id.item_card_weather_temp_max);
            mImageViewIcon = itemView.findViewById(R.id.item_card_weather_icon);
            mTextViewTemp = itemView.findViewById(R.id.item_card_weather_temp);
        }

    }

}
