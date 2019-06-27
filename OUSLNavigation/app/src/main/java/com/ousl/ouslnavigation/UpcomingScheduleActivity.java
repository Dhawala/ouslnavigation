package com.ousl.ouslnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.UpcomingRequest;
import com.ousl.listview.UpcomingScheduleListView;
import com.ousl.listview.ViewScheduleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpcomingScheduleActivity extends AppCompatActivity {

    String course_code[], ac_name[], medium[], group[], date[], start_time[], end_time[], centre[], location[], room[];
    String current_date;

    private LoginSession session;
    ListView listView;
    UpcomingScheduleListView upcomingScheduleListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        current_date = df.format(d);

        session = new LoginSession(getApplicationContext());

        getSupportActionBar().setTitle("Upcoming Activities");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.upcoming_course);
        fillListView();

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

                        course_code = new String[data.length()];
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
                            course_code[i] = record.getString("course_code");
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
                        upcomingScheduleListView = new UpcomingScheduleListView(UpcomingScheduleActivity.this, course_code, ac_name, medium, group, date, start_time, end_time, centre, location, room);
                        listView.setAdapter(upcomingScheduleListView);
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

        UpcomingRequest upcomingRequest = new UpcomingRequest(session.getSID(), current_date, current_date, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(UpcomingScheduleActivity.this);
        queue.add(upcomingRequest);
    }

}
