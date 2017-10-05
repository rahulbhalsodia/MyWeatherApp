package com.example.ronij.myweatherapp;

import android.content.Context;
import android.preference.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class BackFunctions {
    private static final String WEATHER_API = "8ef9ec3db431642e80a74c3c455df667";
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid="+WEATHER_API; //"http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric";
    public static JSONObject getJSON(Context context,String city){
        HttpURLConnection connection=null;
        BufferedReader reader=null;
            try {
                URL url = new URL(String.format(WEATHER_URL, city));
                connection = (HttpURLConnection) url.openConnection();
                //connection.addRequestProperty(WEATHER_API,context.getString(R.string.open_weather_maps_app_id));
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer json = new StringBuffer(1024);
                String tmp;
                while ((tmp = reader.readLine()) != null) {
                    json.append(tmp).append("\n");
                }
                reader.close();
                JSONObject data = new JSONObject(json.toString());
                if (data.getInt("cod") != 200) {
                    JSONObject savedData = new JSONObject(PreferenceManager.getDefaultSharedPreferences(context).getString("myWeatherJson", ""));
                    if (savedData.getInt("cod") != 200) {
                        return null;
                    }
                    return savedData;
                }
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("myWeatherJson", data.toString()).apply();
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                JSONObject savedData = null;
                try {
                    savedData = new JSONObject(PreferenceManager.getDefaultSharedPreferences(context).getString("myWeatherJson", ""));
                    if (savedData.getInt("cod") != 200) {
                        return null;
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return savedData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }finally {

                try {
                    reader.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
    }
}
