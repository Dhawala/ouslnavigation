package com.ousl.ouslnavigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ousl.dbo.BuildingRequest;
import com.ousl.dbo.LocationRequest;
import com.ousl.dbo.RouteRequest;
import com.ousl.util.Constants;
import com.ousl.util.Edge;
import com.ousl.util.LoggedUser;
import com.ousl.util.MapUtil;
import com.ousl.util.ShortestPath;
import com.ousl.util.Vertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView navName = (TextView) header.findViewById(R.id.nav_name);
        TextView navEmail = (TextView) header.findViewById(R.id.nav_email);
        navigationView.setNavigationItemSelectedListener(this);

        navName.setText(LoggedUser.getName());
        navEmail.setText(LoggedUser.getEmail());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        getLocations();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view) {
            if(LoggedUser.getUserLogged()){
                Intent intent=new Intent(HomeActivity.this, ViewScheduleActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_upcoming) {
            if(LoggedUser.getUserLogged()){
                Intent intent=new Intent(HomeActivity.this, UpcomingScheduleActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_announcements) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_aboutus) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng openUni = new LatLng(6.883810, 79.884354);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(openUni, 18));
        mMap.setOnCameraChangeListener(this);

        if(mLocationPermissionGranted){
            getDeviceLoaction();

            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    return;
                }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMinZoomPreference(18);
        }

        mMap.addPolyline(new MapUtil().createPolyline(Constants.UNI_COORDINATES, 8, "#000000")).setWidth(8);
        drawBuildings();
        drawRoutes();

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if(cameraPosition.zoom < 17){
            for(Polygon poly : MapUtil.buildings){
                poly.setVisible(false);
            }
        }
        else{
            for(Polygon poly : MapUtil.buildings){
                poly.setVisible(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch(requestCode){
            case 123:{
                if(grantResults.length > 0){
                    for(int i=0; i<grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getLocationPermission(){
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, 123);
        }
    }

    private void getDeviceLoaction(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.e(this.getClass().getSimpleName(), "Security Error: "+e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void drawBuildings(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        JSONArray buildings = jsonResponse.getJSONArray("buildings");
                        JSONObject record;

                        for(int i=0; i<buildings.length(); i++){
                            record = buildings.getJSONObject(i);
                            String coordinates = record.getString("coordinates");
                            Polygon polygon = mMap.addPolygon(new MapUtil().createPolygon(coordinates));
                            MapUtil.buildings.add(polygon);
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        BuildingRequest buildingRequest = new BuildingRequest(responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(buildingRequest);
    }

    private void getLocations(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if(success){
                        JSONArray buildings = jsonResponse.getJSONArray("locations");
                        JSONObject record;

                        for(int i=0; i<buildings.length(); i++){
                            record = buildings.getJSONObject(i);
                            MapUtil.locations.add(record.getString("loc_name"));
                            MapUtil.location_coordinates.put(record.getString("loc_name"), new LatLng(record.getDouble("longitude"), record.getDouble("latitude")));
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        LocationRequest locationRequest = new LocationRequest(responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(locationRequest);

    }

    private void drawRoutes(){

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    int size = jsonResponse.getInt("max_value");

                    System.out.println(size);

                    MapUtil.routes = new String[size+1][size+1];
                    System.out.println(MapUtil.routes.length);

                    if(success){
                        JSONArray buildings = jsonResponse.getJSONArray("routes");
                        JSONObject record;

                        for(int i=0; i<buildings.length(); i++){
                            record = buildings.getJSONObject(i);
                            int no_from = record.getInt("loc_no_from");
                            int no_to = record.getInt("loc_no_to");
                            int width = record.getInt("width");
                            int weight = record.getInt("weight");
                            String loc_from = record.getString("loc_name_from");
                            String loc_to = record.getString("loc_name_to");
                            String coordinates = record.getString("coordinates");
                            String isMainRoute = record.getString("main_route");

                            System.out.println(isMainRoute);

                            MapUtil.location_numbers.put(loc_from, no_from);
                            MapUtil.location_numbers.put(loc_to, no_to);
                            MapUtil.routes[no_from][no_to] = coordinates;
                            MapUtil.routes[no_to][no_from] = coordinates;

                            ShortestPath.nodes.add(no_from, new Vertex(loc_from.toString()));
                            ShortestPath.nodes.add(no_to, new Vertex(loc_to.toString()));

                            ShortestPath.nodes.get(no_from).adjacencies.add(new Edge(ShortestPath.nodes.get(no_to), weight));
                            ShortestPath.nodes.get(no_to).adjacencies.add(new Edge(ShortestPath.nodes.get(no_from), weight));

                            if(isMainRoute.equals("Yes")){
                                mMap.addPolyline(new MapUtil().createPolyline(coordinates, width+4, "#000000"));
                                mMap.addPolyline(new MapUtil().createPolyline(coordinates, width, "#ffffff"));
                            }

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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG);
            }
        };

        RouteRequest routeRequest = new RouteRequest(responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(routeRequest);

    }
}
