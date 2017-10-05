package com.example.ronij.myweatherapp;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;


public class WeatherFragment extends Fragment {
    Typeface weatherFont;
    TextView cities;
    TextView update;
    TextView details;
    TextView currentTemperature;
    TextView weatherIcon;
    Handler handler;
    private CityDataBaseHelper dataBaseHelper;
    public WeatherFragment(){
        handler=new Handler();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
    View root= inflater.inflate(R.layout.fragment_weather,container,false);
        cities=(TextView)root.findViewById(R.id.city_field);
        update=(TextView)root.findViewById(R.id.updated_field);
        details=(TextView)root.findViewById(R.id.details_field);
        currentTemperature=(TextView)root.findViewById(R.id.current_temperature_field);
        weatherIcon=(TextView)root.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        return root;
    }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        weatherFont=Typeface.createFromAsset(getActivity().getAssets(),"fonts/weathericons-regular-webfont.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    private void updateWeatherData(final String city) {
        Thread thread=new Thread(new Runnable(){
            public void run(){
                final JSONObject json = BackFunctions.getJSON(getActivity(),city);
                if(json==null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.place_not_found), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setWeather(json);
                        }
                    });
                }
            }
        });
        thread.start();
    }
    private void setWeather(JSONObject json){
        String city;
        try {
            city=json.getString("name").toUpperCase(Locale.ENGLISH)+", "+json.getJSONObject("sys").getString("country");
            cities.setText(city);
            JSONObject temp=json.getJSONArray("weather").getJSONObject(0);
            JSONObject main=json.getJSONObject("main");
            details.setText(temp.getString("description").toUpperCase(Locale.ENGLISH)+"\n" +
                    "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Pressure: " + main.getString("pressure") + " hPa");
            currentTemperature.setText(String.format("%.2f",main.getDouble("temp")-273.15)+" ℃");  //use alt+248 for degree
            DateFormat df =DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            update.setText("Last Updated: "+updatedOn);
            setWeatherIcon(temp.getInt("id"),json.getJSONObject("sys").getLong("sunrise")*1000,json.getJSONObject("sys").getLong("sunset")*1000);
            dataBaseHelper = new CityDataBaseHelper(getContext());
            new CityPreference(getActivity()).setCity(city);
            if(dataBaseHelper.getWeather(city)==null) {
                dataBaseHelper.addCity(city, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("myWeatherJson", ""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id=actualId/100;
        String icon="";
        if(actualId==800){
            long currentTime=new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset){
                icon=getContext().getString(R.string.weather_sunny);
            }else{
                icon=getContext().getString(R.string.weather_clear_night);
            }
        }else{
            switch (id){
                case 2:icon=getContext().getString(R.string.weather_thunder);
                    break;
                case 3:icon=getContext().getString(R.string.weather_drizzle);
                    break;
                case 5:icon=getContext().getString(R.string.weather_rainy);
                    break;
                case 6:icon=getContext().getString(R.string.weather_snowy);
                    break;
                case 7:icon=getContext().getString(R.string.weather_foggy);
                    break;
                case 8:icon=getContext().getString(R.string.weather_cloudy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
    public void changeCity(String city){
        updateWeatherData(city);
    }
}
