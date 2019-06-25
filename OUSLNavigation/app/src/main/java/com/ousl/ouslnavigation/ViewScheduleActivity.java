package com.ousl.ouslnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.dbo.ViewRequest;
import com.ousl.listview.ViewScheduleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    String ac_name[], medium[], group[], date[], start_time[], end_time[], centre[], location[], room[];

    ListView listView;
    TextView txtCourseCode;
    Button btnSearch;
    ViewScheduleListView viewScheduleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().setTitle("View Schedule");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.view_course);
        txtCourseCode = (TextView) findViewById(R.id.view_course_code);
        btnSearch = (Button) findViewById(R.id.view_search);

        btnSearch.setOnClickListener(this);
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
        if(view == btnSearch){
            fillListView();
        }
    }

    private void fillListView(){
        final String course_code = txtCourseCode.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        JSONArray data = jsonResponse.getJSONArray("data");
                        JSONObject record;

                        ac_name = new String[data.length()];
                        medium = new String[data.length()];
                        group = new String[data.length()];
                        date = new String[data.length()];
                        start_time = new String[data.length()];
                        end_time = new String[data.length()];
                        centre = new String[data.length()];
                        location = new String[data.length()];
                        room = new String[data.length()];

                        for(int i=0; i<data.length(); i++){
                            record = data.getJSONObject(i);
                            ac_name[i] = record.getString("ac_name");
                            medium[i] = record.getString("medium");
                            group[i] = record.getString("group");
                            date[i] = record.getString("date");
                            start_time[i] = record.getString("start_time");
                            end_time[i] = record.getString("end_time");
                            centre[i] = record.getString("centre");
                            location[i] = record.getString("loc_name");
                            room[i] = record.getString("room_name");
                        }
                        viewScheduleListView = new ViewScheduleListView(ViewScheduleActivity.this, ac_name, medium, group, date, start_time, end_time, centre, location, room);
                        listView.setAdapter(viewScheduleListView);
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

        ViewRequest viewRequest = new ViewRequest(course_code, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(ViewScheduleActivity.this);
        queue.add(viewRequest);
    }
}
