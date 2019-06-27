package com.ousl.ouslnavigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.dbo.RoomRequest;
import com.ousl.listview.RoomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BuildingDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String rooms[];
    Bundle bundle;

    ListView listView;
    RoomListView roomListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buliding_details);

        bundle = getIntent().getExtras();

        getSupportActionBar().setTitle(bundle.getString("location"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.listview_room);
        fillListView();

        listView.setOnItemClickListener(this);

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

    private void fillListView(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        JSONArray data = jsonResponse.getJSONArray("data");
                        JSONObject record;

                        rooms = new String[data.length()];

                        for(int i=0; i<data.length(); i++){
                            record = data.getJSONObject(i);
                            rooms[i] = record.getString("room_name");
                        }
                        roomListView = new RoomListView(BuildingDetailsActivity.this, rooms);
                        listView.setAdapter(roomListView);
                    }
                    else{
                        finish();
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

        RoomRequest roomRequest = new RoomRequest(bundle.getString("location"),  responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(BuildingDetailsActivity.this);
        queue.add(roomRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == listView){
            String room_name = (String) listView.getItemAtPosition(i);
            Intent intent = new Intent(BuildingDetailsActivity.this, RoomDetailsActivity.class);
            intent.putExtra("room_name", room_name);
            startActivity(intent);
        }
    }
}
