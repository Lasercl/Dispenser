package com.example.dispenser.data;

import android.content.Context;
import android.content.SharedPreferences;

public class DispenserLocalDataSource {

    private static final String PREF_NAME = "dispenser_pref";
    private static final String KEY_SELECTED_ID = "selected_dispenser_id";

    private final SharedPreferences prefs;

    public DispenserLocalDataSource(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSelectedDispenserId(String dispenserId) {
        prefs.edit().putString(KEY_SELECTED_ID, dispenserId).apply();
    }

    public String getSelectedDispenserId() {
        return prefs.getString(KEY_SELECTED_ID, null);
    }
}
