package br.edu.example.jonathan.jgsweather.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.Weather;
import br.edu.example.jonathan.jgsweather.utils.OpenWeatherUtils;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<Weather> mWeather = new ArrayList<>();

    private AdapterInteractionListener mListener;

    WeatherAdapter(AdapterInteractionListener interactionListener) {
        this.mListener = interactionListener;
    }

    public WeatherAdapter(List<Weather> weathers, AdapterInteractionListener interactionListener) {
        this(interactionListener);
        mWeather.addAll(weathers);
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_weather, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        final Weather weather = mWeather.get(position);
        if (hasListener()) {
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(weather);
                }
            });
        }
        holder.mTextViewTitle.setText(weather.getCityName());
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

    public void addAll(List<Weather> weathers) {
        mWeather.addAll(weathers);
    }

    public void clear() {
        mWeather.clear();
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

        private TextView mTextViewTitle;

        private TextView mTextViewCondition;

        private TextView mTextViewTempMin;

        private TextView mTextViewTempMax;

        private ImageView mImageViewIcon;

        private TextView mTextViewTemp;

        private CardView mCardView;

        WeatherViewHolder(View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.item_card_weather);
            mTextViewTitle = itemView.findViewById(R.id.item_card_weather_title);
            mTextViewCondition = itemView.findViewById(R.id.item_card_weather_condition);
            mTextViewTempMin = itemView.findViewById(R.id.item_card_weather_temp_min);
            mTextViewTempMax = itemView.findViewById(R.id.item_card_weather_temp_max);
            mImageViewIcon = itemView.findViewById(R.id.item_card_weather_icon);
            mTextViewTemp = itemView.findViewById(R.id.item_card_weather_temp);
        }

    }

    public interface AdapterInteractionListener {

        void onItemClick(Weather weather);

    }

}
