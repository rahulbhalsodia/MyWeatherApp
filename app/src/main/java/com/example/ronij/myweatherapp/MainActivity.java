package com.example.ronij.myweatherapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView drawCity;
    private ArrayAdapter<String> adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CityDataBaseHelper dataBaseHelper;
    private String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawCity=(ListView)findViewById(R.id.cityList);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView=(TextView)view.findViewById(R.id.cityNames);
                String city=textView.getText().toString();
                changeCity(city);
                CityDataBaseHelper dataBaseHelper=new CityDataBaseHelper(getApplicationContext());
                String data = dataBaseHelper.getWeather(city);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("myWeatherJson", data).apply();
                setFragment();
                drawerLayout.closeDrawer(drawCity);
            }
        });
        setFragment();
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }
    private void setFragment(){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        WeatherFragment myFrag = new WeatherFragment();
        transaction.replace(R.id.container,myFrag);
        transaction.addToBackStack("ronij");
        transaction.commit();
        addCitiesToList();
    }
    private void addCitiesToList(){
        ArrayList<String> cities=new ArrayList<String>();
        CityDataBaseHelper dataBaseHelper=new CityDataBaseHelper(getApplicationContext());
        Cursor cursor = dataBaseHelper.getCity();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            cities.add(cursor.getString(1));
            cursor.moveToNext();
        }
        adapter=new ArrayAdapter<String>(this,R.layout.citylist_item,R.id.cityNames,cities);
        drawCity.setAdapter(adapter);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if(item.getItemId()==R.id.change_city){
            if(activeNetworkInfo != null) {
                showInputDialog();
            }else {
                Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
            }
        }else if(item.getItemId()==R.id.refresh){
            if(activeNetworkInfo != null) {
                setFragment();
            }else {
                Toast.makeText(this, "No Internet Available", Toast.LENGTH_SHORT).show();
            }
        }else if(item.getItemId()==R.id.about){

        }
        return false;
    }
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
    private void showInputDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Enter New City");
        final EditText input=new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                        city=input.getText().toString();
                        changeCity(city);
            }
        });
        builder.show();
    }
    private void changeCity(String city){
        WeatherFragment wf=(WeatherFragment)getSupportFragmentManager().findFragmentById(R.id.container);
        wf.changeCity(city);
        new CityPreference(this).setCity(city);
        setFragment();
    }

}
