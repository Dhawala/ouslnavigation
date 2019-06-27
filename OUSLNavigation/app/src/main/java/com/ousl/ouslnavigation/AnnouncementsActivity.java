package com.ousl.ouslnavigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.AnnouncementsRequest;
import com.ousl.dbo.UpcomingRequest;
import com.ousl.listview.AnnouncementsListView;
import com.ousl.listview.UpcomingScheduleListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnnouncementsActivity extends AppCompatActivity {

    String course_code[], ac_name[], message[];

    private LoginSession session;
    private ListView listView;
    private AnnouncementsListView announcementsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        session = new LoginSession(getApplicationContext());

        getSupportActionBar().setTitle("Notices");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = (ListView) findViewById(R.id.listview_notice);
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
                        message = new String[data.length()];

                        for(int i=0; i<data.length(); i++){
                            record = data.getJSONObject(i);
                            course_code[i] = record.getString("course_code");
                            ac_name[i] = record.getString("ac_name");
                            message[i] = record.getString("medium");
                        }
                        announcementsListView = new AnnouncementsListView(AnnouncementsActivity.this, course_code, ac_name, message);
                        listView.setAdapter(announcementsListView);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
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

        AnnouncementsRequest announcementsRequest = new AnnouncementsRequest(session.getSID(), responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(AnnouncementsActivity.this);
        queue.add(announcementsRequest);
    }

}
