package com.ousl.ouslnavigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "LoginActivity";

    private LoginSession session;

    private EditText txtUsername, txtPassword;
    private Button btnSignIn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new LoginSession(getApplicationContext());

        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.signin);

        progressDialog = new ProgressDialog(this);

        btnSignIn.setOnClickListener(this);

        init();
    }

    public void init(){
        Log.d(TAG, "Initializing login activity");

        Log.d(TAG, "Initalizing: setting login activity fullscreen");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        progressDialog.setMessage("Signing In...");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view == btnSignIn){
            loginUser();
        }
    }

    private void loginUser(){
        final String username = txtUsername.getText().toString();
        final String password = txtPassword.getText().toString();

        progressDialog.show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    progressDialog.hide();
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        String sno = jsonResponse.getString("sno");
                        String pass = jsonResponse.getString("password");
                        String name = jsonResponse.getString("name");
                        String email = jsonResponse.getString("email");
                        int contact = jsonResponse.getInt("contact");

                        if(username.equals(sno) && password.equals(pass)){
                            session.createLoginSession(sno, name, email, contact);
                            Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(i);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Invalid Username or Password!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        LoginRequest loginRequest = new LoginRequest(username, password, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }
}
