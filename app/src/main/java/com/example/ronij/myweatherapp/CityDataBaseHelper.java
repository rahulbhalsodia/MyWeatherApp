package com.example.ronij.myweatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;



public class CityDataBaseHelper extends SQLiteOpenHelper {
    private static final String DBNAME="MyWeatherData";
    private Context context;
    public CityDataBaseHelper(Context context){
        super(context, DBNAME, null, 1);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE city_data (_id INTEGER PRIMARY KEY AUTOINCREMENT, city TEXT, data TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS city_data");
        onCreate(db);
    }
    public void addCity(String name,String data)
    {
        ContentValues values=new ContentValues(2);
        values.put("city", name);
        values.put("data", data);
        getWritableDatabase().insert("city_data", null, values);
    }
    public Cursor getCity()
    {
        Cursor cursor = getReadableDatabase().rawQuery("select *  from city_data", null);
        return cursor;
    }
    public String getWeather(String city){
        String data=null;
        Cursor cursor;
        try {
            cursor = getReadableDatabase().query("city_data", new String[]{"_id", "data"}, "city='" + city+"'", null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                data=cursor.getString(1);
                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }
}
