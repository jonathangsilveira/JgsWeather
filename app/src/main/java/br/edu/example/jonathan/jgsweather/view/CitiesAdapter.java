package br.edu.example.jonathan.jgsweather.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.example.jonathan.jgsweather.R;
import br.edu.example.jonathan.jgsweather.model.City;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.CityViewHolder> {

    private List<City> mCities = new ArrayList<>();

    private AdapterInteractionListener mListener;

    public CitiesAdapter(List<City> mCities) {
        this.mCities.addAll(mCities);
    }

    public CitiesAdapter(List<City> mCities, AdapterInteractionListener mListener) {
        this(mCities);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View layout = inflater.inflate(R.layout.item_city, viewGroup, false);
        return new CityViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder viewHolder, int i) {
        final int position = i;
        City city = mCities.get(position);
        String nameAndCountry = String.format("%d - %s, %s", city.getId(), city.getName(), city.getCountry());
        viewHolder.mTextViewNameAndCountry.setText(nameAndCountry);
        if (hasListener()) {
            viewHolder.mImageButtonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDelete(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    public void registerListener(AdapterInteractionListener listener) {
        mListener = listener;
    }

    private boolean hasListener() {
        return mListener != null;
    }

    public void add(City city) {
        mCities.add(city);
        notifyDataSetChanged();
    }

    public void addAll(List<City> cities) {
        mCities.addAll(cities);
        notifyDataSetChanged();
    }

    public void remove(City city) {
        mCities.remove(city);
        notifyDataSetChanged();
    }

    public City get(int position) {
        return mCities.get(position);
    }

    class CityViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNameAndCountry;

        ImageButton mImageButtonDelete;

        CityViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNameAndCountry = itemView.findViewById(R.id.item_city_name_and_country);
            mImageButtonDelete = itemView.findViewById(R.id.item_city_delete);
        }

    }

    public interface AdapterInteractionListener {

        void onDelete(int position);

    }

}
