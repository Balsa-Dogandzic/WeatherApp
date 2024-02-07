package com.example.vremenskaprognoza;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {
    /* Deklaracija varijabli */
    private RelativeLayout homeLayout;
    private ProgressBar loadingBar;
    private ImageView weatherIcon, searchIcon, infoIcon;
    private TextView cityNameView, temperatureView, conditionView;
    private TextInputEditText cityEdit;
    private RecyclerView forecastRView;
    private WeatherAdapter adapter;
    private LocationManager locationManager;
    private final int PERMISSION_CODE = 1;
    private String cityName;
    private Current current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Inicijalizacija varijabli */
        homeLayout = findViewById(R.id.homeLayout);
        loadingBar = findViewById(R.id.loadingBar);
        weatherIcon = findViewById(R.id.weatherIcon);
        searchIcon = findViewById(R.id.searchCity);
        infoIcon = findViewById(R.id.info_icon);
        cityNameView = findViewById(R.id.cityName);
        temperatureView = findViewById(R.id.textTemperature);
        conditionView = findViewById(R.id.weatherCondition);
        cityEdit = findViewById(R.id.cityInput);
        forecastRView = findViewById(R.id.forecastView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        /* Trazenje dozvole za lokaciju od korisnika ukoliko je vec nije unio */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        /* Uzimanje lokacije od korisnika i prikaz vremena na toj lokaciji */
        if (location != null)
            cityName = getCityName(location.getLongitude(), location.getLatitude());
        else
            cityName = "Podgorica";
        getWeatherInfo(cityName);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            /* Prikazuje vrijeme u gradu koji je unesen u tekst polje */
            @Override
            public void onClick(View view) {
                String city = cityEdit.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.city_warning, Toast.LENGTH_SHORT).show();
                } else {
                    getWeatherInfo(city);
                }
            }
        });

        infoIcon.setOnClickListener(new View.OnClickListener() {
            /* Prikazuje alert prozor sa detaljnim informacijama o trenutnom vremenu */
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle(R.string.weather_details);
                builder.setMessage(current.toString());
                builder.show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /* Kada se aplikacija prvi put pokrene trazi dozvolu za lokaciju */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getCityName(double longitude, double latitude) {
        /* Metoda vrace naziv grada za latitudu i longitudu koje joj se proslijede */
        String cityName = "Podgorica";
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitude, longitude, 10);
            for (Address a : addressList) {
                if (a != null) {
                    String city = a.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public void getWeatherInfo(String city) {
        /*Metoda komunicira sa API-jem i mijenja sadrzaj komponenti u odnosu na odgovor API-ja
        ova metoda poziva funkciju populateRV za popunjavanje RecyclerView-a podacima */
        String url = "http://api.weatherapi.com/v1/forecast.json?key=c1b5167a34ad4408a3c121200232803&q=" + city + "&days=1&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingBar.setVisibility(View.GONE);
                        homeLayout.setVisibility(View.VISIBLE);
                        try {
                            //For location and time info
                            JSONObject locationObject = response.getJSONObject("location");
                            String resCity = locationObject.getString("name");
                            String country = locationObject.getString("country");
                            String localTime = locationObject.getString("localtime").split(" ")[1];
                            cityNameView.setText(String.format("%s, %s %s", resCity, country, localTime));
                            //For current weather info
                            JSONObject object = response.getJSONObject("current");
                            String temperature = object.getString("temp_c");
                            String condition = object.getJSONObject("condition").getString("text");
                            String icon = object.getJSONObject("condition").getString("icon");
                            String wind = object.getString("wind_kph");
                            String participation = object.getString("precip_mm");
                            String humidity = object.getString("humidity");
                            String pressure = object.getString("pressure_mb");
                            String feelLike = object.getString("feelslike_c");
                            int is_day = object.getInt("is_day");

                            current = new Current(temperature, wind, feelLike, is_day, pressure, condition, participation, icon, humidity);

                            temperatureView.setText(current.getTemperature());
                            conditionView.setText(current.getCondition());
                            Picasso.get().load("http:".concat(current.getIcon())).into(weatherIcon);

                            populateRV(city);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.api_call_error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void populateRV(String city) {
        /* Metoda koja popunjava RV sa karticama koje sadrze podatke koji se dobijaju sa API-jevog
        endpointa za vremensku prognozu */
        String url = "http://api.weatherapi.com/v1/forecast.json?key=c1b5167a34ad4408a3c121200232803&q=" + city + "&days=3&alerts=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<WeatherModal> weatherModals = new ArrayList<>();
                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONArray forecastDay = forecast.getJSONArray("forecastday");
                    for (int i = 0; i < forecastDay.length(); i++) {
                        JSONArray hour = forecastDay.getJSONObject(i).getJSONArray("hour");
                        for (int j = 0; j < hour.length(); j++) {
                            if (j % 2 == 0) {
                                String icon = hour.getJSONObject(j).getJSONObject("condition").getString("icon");
                                String windSpeed = hour.getJSONObject(j).getString("wind_kph");
                                String temp = hour.getJSONObject(j).getString("temp_c");
                                String time = hour.getJSONObject(j).getString("time");
                                WeatherModal weatherModal;
                                if (i == 0)
                                    weatherModal = new WeatherModal(time, temp, icon, windSpeed, R.string.today);
                                else if (i == 1)
                                    weatherModal = new WeatherModal(time, temp, icon, windSpeed, R.string.tommorow);
                                else
                                    weatherModal = new WeatherModal(time, temp, icon, windSpeed, R.string.day_after);
                                weatherModals.add(weatherModal);
                            }
                        }
                    }
                    adapter = new WeatherAdapter(MainActivity.this, weatherModals);
                    forecastRView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, R.string.api_call_error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}