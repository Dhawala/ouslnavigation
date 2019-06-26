package com.ousl.ouslnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ousl.SessionManagement.LoginSession;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    LoginSession session;

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
        if(view == mBtnLogout){
            session.logoutUser();
        }
        else if(view == mLlEmail){

        }
    }

    private void setValues(){

    }
}
