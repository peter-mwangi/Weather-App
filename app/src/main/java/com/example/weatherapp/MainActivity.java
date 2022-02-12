package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private RelativeLayout homeLayout;
    private ProgressBar progressBar;
    private TextView cityName, tempValue, weatherCondition;
    private TextInputEditText cityNameEditText;
    private RecyclerView forecastRecyclerView;
    private ImageView searchIcon, weatherIcon, backgroundImg;

    private ArrayList<WeatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    private LocationManager locationManager;
    private  int PERMISSION_CODE = 1;
    private String mCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeLayout = findViewById(R.id.home_layout);
        progressBar = findViewById(R.id.progress_bar);
        forecastRecyclerView = findViewById(R.id.recycler_view);
        cityName = findViewById(R.id.city_name);
        tempValue = findViewById(R.id.temp_text);
        weatherCondition = findViewById(R.id.condition_text);
        cityNameEditText = findViewById(R.id.city_name_input);
        searchIcon = findViewById(R.id.search_icon);
        weatherIcon = findViewById(R.id.weather_icon);
        backgroundImg = findViewById(R.id.background_img);

        weatherModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModelArrayList);
        forecastRecyclerView.setAdapter(weatherAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mCityName = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(mCityName);

        searchIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String city = cityNameEditText.getText().toString();
                if (city.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Enter City Name", Toast.LENGTH_LONG).show();
                }
                else
                {
                    cityNameEditText.setText(city);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Please Provide the Requested Permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude)
    {
        String mCityName = "Not Found";

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try
        {
            List<Address> addresses = gcd.getFromLocation(longitude, latitude, 10);
            for (Address adr: addresses)
            {
                if (adr != null)
                {
                    String city = adr.getLocality();
                    if (city != null && !city.equals(""))
                    {
                        mCityName = city;
                    }
                    else
                    {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return mCityName;
    }

    private void getWeatherInfo(String mCityName)
    {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=fe8ffe3b119c44cd8f4210257221102&q="+mCityName+"&days=1&aqi=no&alerts=no";
        cityName.setText(mCityName);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                homeLayout.setVisibility(View.VISIBLE);
                weatherModelArrayList.clear();

                try {
                    String temp = response.getJSONObject("current").getString("temp_c");
                    tempValue.setText(temp + "°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(weatherIcon);
                    weatherCondition.setText(condition);

                    if (isDay == 1)
                    {
                        Picasso.get().load("https://images.unsplash.com/photo-1517685633466-403d6955aeab?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backgroundImg);
                    }
                    else
                    {
                        Picasso.get().load("https://images.unsplash.com/photo-1532767153582-b1a0e5145009?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backgroundImg);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArry = forecast0.getJSONArray("hour");

                    for (int i =0; i<hourArry.length(); i++)
                    {
                        JSONObject hourObj = hourArry.getJSONObject(i);
                        String dTime = hourObj.getString("time");
                        String dTemp = hourObj.getString("temp_c");
//                        String condText = hourObj.getJSONObject("condition").getString("text");
                        String condIcon = hourObj.getJSONObject("condition").getString("icon");
                        String windSpeed = hourObj.getString("wind_kph");
                        weatherModelArrayList.add(new WeatherModel(dTime, dTemp, windSpeed, condIcon));
                    }
                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MainActivity.this, "Enter a Valid City Name", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }
}