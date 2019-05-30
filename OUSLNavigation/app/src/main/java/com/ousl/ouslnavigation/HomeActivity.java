package com.ousl.ouslnavigation;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
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
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnCameraChangeListener, View.OnClickListener {

    //static final variables
    private static final String TAG = "HomeActivity:";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    //Activity widgets declaration
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private FloatingActionButton mFloatingActionButtonMessage;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private AutoCompleteTextView mSearchTextTo;
    private ImageButton mSearchButtonTo;
    private AutoCompleteTextView mSearchTextFrom;
    private ImageButton mSearchButtonFrom;
    private RelativeLayout mSearchFromLayout;

    //variables
    private Boolean mLocationPermissionGranted = false;
    //private MarkerOptions mMarkerOptionsTo = null;
    private Marker mMarkerTo = null;
    private Marker mMarkerFrom = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFloatingActionButtonMessage = (FloatingActionButton) findViewById(R.id.fab);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mSearchTextTo = (AutoCompleteTextView) findViewById(R.id.txt_search_to);
        mSearchButtonTo = (ImageButton) findViewById(R.id.btn_next_search_to);
        mSearchTextFrom = (AutoCompleteTextView) findViewById(R.id.txt_search_from);
        mSearchButtonFrom = (ImageButton) findViewById(R.id.btn_next_search_from);
        mSearchFromLayout = (RelativeLayout) findViewById(R.id.relLayout2);

        if(isServicesOK()){
            init();
        }

    }

    private void init(){
        Log.d(TAG, "Initializing Activity...");

        Log.d(TAG, "Initializing: setting activity fullscreen");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d(TAG, "Initializing: hide mToolbar");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mFloatingActionButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Log.d(TAG, "Initializing: action bar mDrawer is set to be not toggled");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d(TAG, "Initializing: navigation view - set student loged in and photo");
        View header = mNavigationView.getHeaderView(0);
        TextView navName = (TextView) header.findViewById(R.id.nav_name);
        TextView navEmail = (TextView) header.findViewById(R.id.nav_email);
        mNavigationView.setNavigationItemSelectedListener(this);

        navName.setText(LoggedUser.getName());
        navEmail.setText(LoggedUser.getEmail());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        getLocations();

        ArrayAdapter<String> locatonsAdaptor = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, MapUtil.locations);
        mSearchTextTo.setAdapter(locatonsAdaptor);
        mSearchButtonTo.setOnClickListener(this);

        mSearchFromLayout.setVisibility(View.INVISIBLE);
        mSearchTextFrom.setAdapter(locatonsAdaptor);
        mSearchButtonFrom.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(buttonClick);
        String loc;

        if(view == mSearchButtonTo) {
            loc = mSearchTextTo.getText().toString();
            if(mMarkerTo == null){
                mMarkerTo = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));
            }
            setMarker(mMarkerTo, loc);
            mSearchFromLayout.setVisibility(View.VISIBLE);
        }
        else if(view == mSearchButtonFrom){
            loc = mSearchTextFrom.getText().toString();
            if(mMarkerFrom == null){
                mMarkerFrom = mMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).visible(false));
            }
            setMarker(mMarkerFrom, loc);
            drawRoute(mMarkerFrom.getTitle(), mMarkerTo.getTitle());
        }
    }

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

    //class methods
    public boolean isServicesOK(){
        Log.d(TAG, "isServiceOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(HomeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOK: Google Play Services are available");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOK: an error occurred, but it can be resolved");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(HomeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Log.d(TAG, "Map requests are disabled");
            Toast.makeText(this, "Map Reqests are disabled.", Toast.LENGTH_SHORT);
        }
        return false;
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
                        JSONArray location = jsonResponse.getJSONArray("locations");
                        JSONObject record;

                        for(int i=0; i<location.length(); i++){
                            record = location.getJSONObject(i);
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

                            if(ShortestPath.nodes.get(no_from) == null) {
                                ShortestPath.nodes.add(no_from, new Vertex(loc_from.toString()));
                            }
                            if(ShortestPath.nodes.get(no_to) == null) {
                                ShortestPath.nodes.add(no_to, new Vertex(loc_to.toString()));
                            }

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

    private void drawRoute(String from, String to){
        System.out.println(from);
        System.out.println(to);
        int no = MapUtil.location_numbers.get(from);
        to = "Junction 1";
        ShortestPath.computePaths(ShortestPath.nodes.get(MapUtil.location_numbers.get(from)));
        List<Vertex> path = ShortestPath.getShortestPathTo(ShortestPath.nodes.get(MapUtil.location_numbers.get(to)));

//        int no1 = MapUtil.location_numbers.get(path.get(0).toString());
//        mMap.addPolyline(new MapUtil().createPolyline(MapUtil.routes[no][no1], 9, "#FF0000"));
        System.out.println(no);
        System.out.println(path);
        System.out.println(path.get(0));
        System.out.println(MapUtil.location_numbers.get(path.get(0).toString()));
        for(int i=0; i<path.size(); i++){
            //String c = MapUtil.routes[i][i+1];
           // mMap.addPolyline(new MapUtil().createPolyline(c, 9, "#FF0000"));
        }
    }

    private void setMarker(Marker m, String loc){
        try {
            LatLng latLng = MapUtil.location_coordinates.get(loc);
            m.setPosition(latLng);
            m.setTitle(loc);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            m.setVisible(true);
        }
        catch(IllegalArgumentException e){
            Toast.makeText(this, "Invalid location", Toast.LENGTH_LONG).show();
        }
    }

}
