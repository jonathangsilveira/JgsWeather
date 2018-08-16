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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.edu.example.jonathan.jgsweather.utils.OpenWeatherUtils;
import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Weather;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private Context mContext;

    private List<Weather> mWeather = new ArrayList<>();

    private NameComparator mByName;

    private TemperatureMinCompatator mByMin;

    private TemperatureMaxCompatator mByMax;

    private AdapterInteractionListener mListener;

    public WeatherAdapter(List<Weather> weathers) {
        mWeather.addAll(weathers);
    }

    public WeatherAdapter(List<Weather> weathers, AdapterInteractionListener mListener) {
        this(weathers);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        final Weather weather = mWeather.get(position);
        if (hasListener()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(weather);
                }
            });
        }
        holder.textViewCity.setText(weather.getCityName());
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

    public void addAll(List<Weather> weathers) {
        mWeather.addAll(weathers);
    }

    public void clear() {
        mWeather.clear();
        notifyDataSetChanged();
    }

    public void sortByName() {
        if (mByName == null) {
            mByName = new NameComparator();
        }
        Collections.sort(mWeather, mByName);
        notifyDataSetChanged();
    }

    public void sortByMin() {
        if (mByMin == null) {
            mByMin = new TemperatureMinCompatator();
        }
        Collections.sort(mWeather, mByMin);
        notifyDataSetChanged();
    }

    public void sortByMax() {
        if (mByMax == null) {
            mByMax = new TemperatureMaxCompatator();
        }
        Collections.sort(mWeather, mByMax);
        notifyDataSetChanged();
    }

    private boolean hasListener() {
        return mListener != null;
    }

    @Override
    public int getItemCount() {
        return mWeather.size();
    }

    class WeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCity;

        private TextView textViewCondition;

        private TextView textViewMin;

        private TextView textViewMax;

        private ImageView imageViewIcon;

        private TextView textViewTemp;

        WeatherViewHolder(View itemView) {
            super(itemView);
            textViewCity = itemView.findViewById(R.id.item_weather_city);
            textViewCondition = itemView.findViewById(R.id.item_weather_status);
            textViewMin = itemView.findViewById(R.id.item_weather_min);
            textViewMax = itemView.findViewById(R.id.item_weather_max);
            imageViewIcon = itemView.findViewById(R.id.item_weather_image);
            textViewTemp = itemView.findViewById(R.id.item_weather_temp);
        }

    }

    private class NameComparator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            String thisName = thisOne.getCityName();
            String thatName = thatOne.getCityName();
            return thisName.compareToIgnoreCase(thatName);
        }

    }

    private class TemperatureMinCompatator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            Double thisMin = thisOne.getTemperatureMin();
            Double thatMin = thatOne.getTemperatureMin();
            return thisMin.compareTo(thatMin);
        }

    }

    private class TemperatureMaxCompatator implements Comparator<Weather> {

        @Override
        public int compare(Weather thisOne, Weather thatOne) {
            Double thisMax = thisOne.getTemperatureMax();
            Double thatMax = thatOne.getTemperatureMax();
            return thatMax.compareTo(thisMax);
        }

    }

    public interface AdapterInteractionListener {

        void onItemClick(Weather weather);

    }

}
