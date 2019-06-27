package com.ousl.ouslnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.StudentDetailsRequest;
import com.ousl.dbo.UpdateStudentDetailsRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private LoginSession session;
    private AlphaAnimation buttonClick;

    private TextView mTxtLable;
    private EditText mTxtValue;
    private Button mBtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_details);

        session = new LoginSession(getApplicationContext());

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        buttonClick = new AlphaAnimation(1F, 0.8F);
        buttonClick.setDuration(500);

        mTxtLable = (TextView) findViewById(R.id.update_txt_detail);
        mTxtValue = (EditText) findViewById(R.id.update_txt_detailvalue);
        mBtnSave = (Button) findViewById(R.id.update_btn_save);

        init();
    }

    private void init(){
        mTxtLable.setText(getIntent().getStringExtra("label"));
        mTxtValue.setText(getIntent().getStringExtra("value"));
        mBtnSave.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(UpdateDetailsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(buttonClick);

        if(view == mBtnSave){
            if(mTxtLable.getText().equals("Email")){
                updateField("email");
            }
            else if(mTxtLable.getText().equals("Phone")){
                updateField("contact");
            }
        }
    }

    private void updateField(String field){

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");

                        if(success){
                            finish();
                            Intent intent = new Intent(UpdateDetailsActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), mTxtLable.getText()+" updated successfully.", Toast.LENGTH_LONG).show();
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

            UpdateStudentDetailsRequest updateDetailsActivity = new UpdateStudentDetailsRequest(field, mTxtValue.getText().toString(), session.getSID(),  responseListener, errorListener);
            RequestQueue queue = Volley.newRequestQueue(UpdateDetailsActivity.this);
            queue.add(updateDetailsActivity);
    }

}
