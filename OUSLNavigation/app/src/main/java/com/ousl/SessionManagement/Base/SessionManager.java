package com.ousl.SessionManagement.Base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    public SharedPreferences sharedPreferences;
    public Editor editor;
    public Context context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "OUSLNavigationPreferences";

    public SessionManager(Context c){
        this.context = c;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

}
