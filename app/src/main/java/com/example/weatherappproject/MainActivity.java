package com.example.weatherappproject;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tempText, citynameTV, conditionTV;
    ImageView iconIV;

    EditText cityNameET;
    ImageButton searchIconIV;
    ProgressBar loadingPB;
    RelativeLayout relativeLayout;
    RecyclerView forcastRecycler;
    ArrayList<forcastHoursRecyclerModel> forcastArrayList;
    String cityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        tempText = findViewById(R.id.temperatureTV);
        iconIV = findViewById(R.id.iconIV);
        citynameTV = findViewById(R.id.citynameTV);
        conditionTV = findViewById(R.id.conditionTV);
        loadingPB = findViewById(R.id.loadingPB);
        relativeLayout = findViewById(R.id.homeRL);
        cityNameET = findViewById(R.id.cityInputET);
        searchIconIV = findViewById(R.id.searchIconIV);
        forcastRecycler = findViewById(R.id.recyclerRV);
        forcastArrayList = new ArrayList<>();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cityName = getCityName(location.getLongitude(), location.getLatitude());
        getWeatherInfo(cityName);

        searchIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityName = cityNameET.getText().toString();
                getWeatherInfo(cityName);
            }
        });

    }

    public String getCityName(double longitudeX, double latitudeX){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

        try{
            List<Address> addresses = gcd.getFromLocation(latitudeX, longitudeX, 10);

            for(Address add : addresses){
                if(add!=null){
                    String city = add.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName =city;
                    }else{
                        Log.d("City", "City not found!");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public void getWeatherInfo(String cityName){
        citynameTV.setText(cityName);
        String url = "https://api.weatherapi.com/v1/forecast.json?key=[YOUR API KEY]&q="+cityName+"&days=1&aqi=yes&alerts=no";
        forcastArrayList.clear();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                loadingPB.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);

                try {
                   String temp = response.getJSONObject("current").getString("temp_c");
                   tempText.setText(temp+"°C");
                   String getIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                   Picasso.get().load("https://".concat(getIcon)).into(iconIV);
                   String getCondition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                   conditionTV.setText(getCondition);

                   JSONObject forcastObj = response.getJSONObject("forecast");
                   JSONObject forcastArrObj = forcastObj.getJSONArray("forecastday").getJSONObject(0);
                   JSONArray forcastArr = forcastArrObj.getJSONArray("hour");

                   for(int i=0;i<forcastArr.length();i++){
                       JSONObject hourObj = forcastArr.getJSONObject(i);
                       String time = hourObj.getString("time");
                       int temp_c = hourObj.getInt("temp_c");
                       int wind_kph = hourObj.getInt("wind_kph");
                       String condition = hourObj.getJSONObject("condition").getString("text");
                       String icon = hourObj.getJSONObject("condition").getString("icon");
                       forcastArrayList.add(new forcastHoursRecyclerModel(time, icon, condition, temp_c, wind_kph));
                   }

                   forcastHoursRecyclerAdapter adapter = new forcastHoursRecyclerAdapter(MainActivity.this, forcastArrayList);
                   forcastRecycler.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tempText.setText("Not found!");
            }
        });
        queue.add(jsonObjectRequest);
    }
}