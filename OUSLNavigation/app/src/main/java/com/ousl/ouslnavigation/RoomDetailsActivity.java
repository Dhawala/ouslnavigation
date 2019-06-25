package com.ousl.ouslnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.dbo.RoomDetailsRequest;
import com.ousl.dbo.RoomRequest;
import com.ousl.listview.RoomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomDetailsActivity extends AppCompatActivity {

    String room;

    private TextView mTxtRoomDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        room = getIntent().getStringExtra("room_name");

        getSupportActionBar().setTitle(room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mTxtRoomDetails = (TextView) findViewById(R.id.txt_room_detail);

        showRoomDetails();
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

    private void showRoomDetails(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        String desc = jsonResponse.getString("desc");
                        mTxtRoomDetails.setText(desc);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Invalid Query!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        RoomDetailsRequest roomDetailsRequest = new RoomDetailsRequest(room,  responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(RoomDetailsActivity.this);
        queue.add(roomDetailsRequest);
    }

}
