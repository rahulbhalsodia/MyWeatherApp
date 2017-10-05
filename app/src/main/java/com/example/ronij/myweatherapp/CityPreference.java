package com.example.ronij.myweatherapp;

import android.app.Activity;
import android.content.SharedPreferences;


public class CityPreference {
    SharedPreferences preferences;
    public CityPreference(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }
        String getCity(){
            return preferences.getString("lastcity", "Sydney, AU");
        }
        void setCity(String city){
        preferences.edit().putString("lastcity",city).commit();
    }
}
