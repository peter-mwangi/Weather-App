package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>
{
    private Context context;
    private ArrayList<WeatherModel> weatherModelArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position)
    {
        WeatherModel model = weatherModelArrayList.get(position);

        holder.timeTxt.setText(model.getTime());
        holder.tempTxt.setText(model.getTemp()+"°c");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.wIcon);
        holder.windSpeedTxt.setText(model.getWindSpeed()+"Km/hr");

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");

        try
        {
            Date date = inputFormat.parse(model.getTime());
            holder.timeTxt.setText(outputFormat.format(date));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView timeTxt, tempTxt, windSpeedTxt;
        private ImageView wIcon;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            timeTxt = itemView.findViewById(R.id.time_text);
            tempTxt = itemView.findViewById(R.id.temp_text);
            windSpeedTxt = itemView.findViewById(R.id.wind_speed_text);
            wIcon = itemView.findViewById(R.id.condition_icon);
        }
    }
}
