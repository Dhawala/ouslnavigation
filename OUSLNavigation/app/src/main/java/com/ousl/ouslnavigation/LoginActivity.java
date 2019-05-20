package com.ousl.ouslnavigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.dbo.LoginRequest;
import com.ousl.util.LoggedUser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtUsername, txtPassword;
    Button btnSignIn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().setTitle("Log In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
        btnSignIn = (Button) findViewById(R.id.signin);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        btnSignIn.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                        int regno = jsonResponse.getInt("regno");
                        String nic = jsonResponse.getString("nic");
                        String name = jsonResponse.getString("name");
                        String email = jsonResponse.getString("email");
                        int contact = jsonResponse.getInt("contact");

                        if(username.equals(sno) && password.equals(pass)){
                            LoggedUser.setUserLogged(true);
                            LoggedUser.setSNo(sno);
                            LoggedUser.setRegNo(regno);
                            LoggedUser.setNIC(nic);
                            LoggedUser.setName(name);
                            LoggedUser.setEmail(email);
                            LoggedUser.setContact(contact);
                            Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
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
