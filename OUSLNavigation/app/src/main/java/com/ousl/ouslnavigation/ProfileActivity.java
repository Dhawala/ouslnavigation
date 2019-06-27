package com.ousl.ouslnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.RoomDetailsRequest;
import com.ousl.dbo.StudentDetailsRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginSession session;
    private AlphaAnimation buttonClick;

    private LinearLayout mLlEmail, mLlPhone, mLlPwd;
    private TextView mTxtSID, mTxtName, mTxtEmail, mTxtPhone;
    private Button mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        session = new LoginSession(getApplicationContext());

        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        buttonClick = new AlphaAnimation(1F, 0.8F);
        buttonClick.setDuration(500);

        mLlEmail = (LinearLayout) findViewById(R.id.profile_area_email);
        mLlPhone = (LinearLayout) findViewById(R.id.profile_area_contact);
        mLlPwd = (LinearLayout) findViewById(R.id.profile_area_pwd);
        mTxtSID = (TextView) findViewById(R.id.profile_txt_sid);
        mTxtName = (TextView) findViewById(R.id.profile_txt_name);
        mTxtEmail = (TextView) findViewById(R.id.profile_txt_email);
        mTxtPhone = (TextView) findViewById(R.id.profile_txt_phone);
        mBtnLogout = (Button) findViewById(R.id.profile_btn_logout);

        init();

    }

    private void init(){
        setValues();

        mBtnLogout.setOnClickListener(this);
        mLlEmail.setOnClickListener(this);
        mLlPhone.setOnClickListener(this);
        mLlPwd.setOnClickListener(this);
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
        view.startAnimation(buttonClick);

        if(view == mBtnLogout){
            session.logoutUser();
        }
        else if(view == mLlEmail){
            finish();
            Intent intent = new Intent(ProfileActivity.this, UpdateDetailsActivity.class);
            intent.putExtra("label", "Email");
            intent.putExtra("value", mTxtEmail.getText().toString());
            startActivity(intent);
        }
        else if(view == mLlPhone){
            finish();
            Intent intent = new Intent(ProfileActivity.this, UpdateDetailsActivity.class);
            intent.putExtra("label", "Phone");
            intent.putExtra("value", mTxtPhone.getText().toString());
            startActivity(intent);
        }
        else if(view == mLlPwd){
            Intent intent = new Intent(ProfileActivity.this, UpdateDetailsActivity.class);
            intent.putExtra("label", "Email");
            intent.putExtra("value", mTxtEmail.getText().toString());
            startActivity(intent);
        }
    }

    private void setValues(){

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            String sno = jsonResponse.getString("sno");
                            String name = jsonResponse.getString("name");
                            String email = jsonResponse.getString("email");
                            String contact = jsonResponse.getString("contact");

                            mTxtSID.setText(sno);
                            mTxtName.setText(name);
                            mTxtEmail.setText(email);
                            mTxtPhone.setText(contact);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            };

            StudentDetailsRequest studentDetailsRequest = new StudentDetailsRequest(session.getSID(),  responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
            queue.add(studentDetailsRequest);

    }
}
