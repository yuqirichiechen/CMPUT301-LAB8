package com.example.listycity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private ArrayList<String> cityList;

    public CityAdapter(ArrayList<String> cityList) {
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        String city = cityList.get(position);
        holder.cityTextView.setText(city);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView cityTextView;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
