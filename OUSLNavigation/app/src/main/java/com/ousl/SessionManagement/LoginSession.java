package com.ousl.SessionManagement;

import android.content.Context;
import android.content.Intent;

import com.ousl.SessionManagement.Base.SessionManager;
import com.ousl.ouslnavigation.LoginActivity;

public class LoginSession extends SessionManager {

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_SID = "sid";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contact";

    public LoginSession(Context c) {
        super(c);
    }

    public String getName(){
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getEmail(){
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getSID(){
        return sharedPreferences.getString(KEY_SID, null);
    }

    public void createLoginSession(String sid, String name, String email, int contact){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_SID, sid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putInt(KEY_CONTACT, contact);

        editor.commit();
    }

    public void checkLogin(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

}
