package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    private Context _context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "pref_name";
    public static final String CITY_NAME = "cityName";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLastSearchDetails(String cityName) {
        editor.putString(CITY_NAME, cityName);
        editor.commit();
    }

    public HashMap<String, String> getLastSearchDetails() {
        HashMap<String, String> details = new HashMap<>();
        details.put(CITY_NAME, pref.getString(CITY_NAME, null));
        return details;
    }


}
