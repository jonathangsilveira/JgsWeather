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

import br.edu.example.jonathan.jgsweather.utils.OpenWeatherUtils;
import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Forecast;
import br.edu.example.jonathan.jgsweather.utils.DateUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    private Context mContext;

    private List<Forecast> mForecast = new ArrayList<>();

    public ForecastAdapter(List<Forecast> weathers) {
        mForecast.addAll(weathers);
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final Forecast weather = mForecast.get(position);
        Date date = weather.getCurrentDate();
        String dateForecast = DateUtils.getDay(date);
        holder.textViewDate.setText(dateForecast);
        holder.textViewCondition.setText(mContext.getString(R.string.label_weather, weather.getDescription()));
        DecimalFormat decimal = (DecimalFormat) NumberFormat.getInstance();
        decimal.setMinimumFractionDigits(2);
        decimal.setMaximumFractionDigits(2);
        holder.textViewMin.setText(mContext.getString(R.string.label_min,
                decimal.format(weather.getTemperatureMin())));
        holder.textViewMax.setText(mContext.getString(R.string.label_max,
                decimal.format(weather.getTemperatureMax())));
        holder.textViewTemp.setText(mContext.getString(R.string.label_current,
                decimal.format(weather.getTemperature())));
        int iconResource = OpenWeatherUtils.getWeatherIcon(weather.getWeatherCode());
        holder.imageViewIcon.setImageResource(iconResource);
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

        private TextView textViewDate;

        private TextView textViewCondition;

        private TextView textViewMin;

        private TextView textViewMax;

        private ImageView imageViewIcon;

        private TextView textViewTemp;

        ForecastViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.item_forecast_date);
            textViewCondition = itemView.findViewById(R.id.item_forecast_status);
            textViewMin = itemView.findViewById(R.id.item_forecast_min);
            textViewMax = itemView.findViewById(R.id.item_forecast_max);
            imageViewIcon = itemView.findViewById(R.id.item_forecast_image);
            textViewTemp = itemView.findViewById(R.id.item_forecast_temp);
        }

    }

}
