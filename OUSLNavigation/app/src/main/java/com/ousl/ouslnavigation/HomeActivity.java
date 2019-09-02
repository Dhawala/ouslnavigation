package com.ousl.ouslnavigation;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ousl.SessionManagement.LoginSession;
import com.ousl.dbo.BuildingRequest;
import com.ousl.dbo.LocationRequest;
import com.ousl.dbo.RouteRequest;
import com.ousl.util.Constants;
import com.ousl.util.Graph.Edge;
import com.ousl.util.MapUtil;
import com.ousl.util.Graph.ShortestPath;
import com.ousl.util.Graph.Vertex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
            GoogleMap.OnCameraChangeListener, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    //static final variables
    private static final String TAG = "HomeActivity:";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private LoginSession session;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private AlphaAnimation buttonClick;

    //Activity widgets declaration
    private GoogleMap mMap;
    private RelativeLayout mSearchLayout;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private AutoCompleteTextView mSearchTextTo, mSearchTextFrom;
    private Button mBtnNavigation;
    private ImageButton mIBtnMenu, mIBtnCurLoc, mIBtnUniLoc;

    //variables
    private Boolean mLocationPermissionGranted = false;
    private LatLng openUni;
    private Marker mMarkerTo = null;
    private ArrayList<Polyline> mPolylineRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new LoginSession(getApplicationContext());

        openUni = new LatLng(6.883810, 79.884354);

        buttonClick = new AlphaAnimation(1F, 0.8F);
        buttonClick.setDuration(500);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSearchLayout = (RelativeLayout) findViewById(R.id.layout_search);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mSearchTextTo = (AutoCompleteTextView) findViewById(R.id.txt_search_to);
        mSearchTextFrom = (AutoCompleteTextView) findViewById(R.id.txt_search_from);
        mBtnNavigation = (Button) findViewById(R.id.btn_navigate);
        mIBtnMenu = (ImageButton) findViewById(R.id.ibtn_menu);
        mIBtnCurLoc = (ImageButton) findViewById(R.id.ibtn_curloc);
        mIBtnUniLoc = (ImageButton) findViewById(R.id.ibtn_uniloc);

        mPolylineRoute = new ArrayList<>();

        session.checkLogin();

        if(isServicesOK()){
            init();
        }
    }

    private void init(){
        Log.d(TAG, "Initializing Activity...");

        MapUtil.location_numbers.clear();
        MapUtil.location_coordinates.clear();
        MapUtil.routes = null;
        MapUtil.locations.clear();
        MapUtil.buildings.clear();

        Log.d(TAG, "Initializing: setting activity fullscreen");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d(TAG, "Initializing: action bar mDrawer is set to be not toggled");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d(TAG, "Initializing: navigation view - set student loged in and photo");
        View header = mNavigationView.getHeaderView(0);
        TextView navName = (TextView) header.findViewById(R.id.nav_name);
        TextView navEmail = (TextView) header.findViewById(R.id.nav_email);

        navName.setText(session.getName());
        navEmail.setText(session.getEmail());
        mNavigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocationPermission();
        getLocations();

        ArrayAdapter<String> locatonsAdaptor = null;
        locatonsAdaptor = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, MapUtil.locations);

        mSearchTextFrom.setAdapter(locatonsAdaptor);
        mSearchTextTo.setAdapter(locatonsAdaptor);

        mBtnNavigation.setOnClickListener(this);

        mIBtnMenu.setOnClickListener(this);
        mIBtnCurLoc.setOnClickListener(this);
        mIBtnUniLoc.setOnClickListener(this);

        HideSoftKeyboard(mSearchTextFrom);
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

        if(view == mBtnNavigation) {
            try {
                String locTo = mSearchTextTo.getText().toString();
                String locFrom = mSearchTextFrom.getText().toString();
                if (mMarkerTo == null) {
                    mMarkerTo = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).visible(false));
                }
                setMarker(mMarkerTo, locTo);
                mMarkerTo.setVisible(true);
                mMarkerTo.showInfoWindow();
                drawRoute(locFrom, locTo);
                HideSoftKeyboard(mSearchTextTo);
            }
            catch(NullPointerException e){
                //do nothing
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(view == mIBtnMenu){
            mDrawer.openDrawer(mNavigationView);
        }
        else if(view == mIBtnCurLoc){
            moveCameraToCurrentLocation();
        }
        else if(view == mIBtnUniLoc){
            moveCameraToUniversity();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(mMarkerTo)){
            Intent i = new Intent(HomeActivity.this, BuildingDetailsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("location", mMarkerTo.getTitle());

            i.putExtras(bundle);
            startActivity(i);
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view) {
                Intent intent=new Intent(HomeActivity.this, ViewScheduleActivity.class);
                startActivity(intent);

        } else if (id == R.id.nav_upcoming) {
                Intent intent=new Intent(HomeActivity.this, UpcomingScheduleActivity.class);
                startActivity(intent);
        } else if (id == R.id.nav_announcements) {
            Intent intent=new Intent(HomeActivity.this, AnnouncementsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            Intent intent=new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(openUni, 18));
        mMap.setOnCameraChangeListener(this);
        mMap.setOnMarkerClickListener(this);

        if(mLocationPermissionGranted){
            getDeviceLocation();

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

        try {
            mMap.addPolyline(new MapUtil().createPolyline(Constants.UNI_COORDINATES, 8, "#000000")).setWidth(8);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void getDeviceLocation(){
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

    private void moveCameraToCurrentLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        }
                    }
                });
            }
        }
        catch (SecurityException e){
            Log.e(this.getClass().getSimpleName(), "Security Error: "+e.getMessage());
        }
    }

    private void moveCameraToUniversity(){
        moveCamera(openUni);
    }

    private void moveCamera(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
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

                            try {
                                if (ShortestPath.nodes.get(no_from) == null) {
                                    ShortestPath.nodes.add(no_from, new Vertex(loc_from.toString()));
                                }
                            }
                            catch(IndexOutOfBoundsException e){
                                ShortestPath.nodes.add(no_from, new Vertex(loc_from.toString()));
                            }

                            try {
                                if (ShortestPath.nodes.get(no_to) == null) {
                                    ShortestPath.nodes.add(no_to, new Vertex(loc_to.toString()));
                                }
                            }
                            catch(IndexOutOfBoundsException e){
                                ShortestPath.nodes.add(no_to, new Vertex(loc_to.toString()));
                            }

                            ShortestPath.nodes.get(no_from).adjacencies.add(new Edge(ShortestPath.nodes.get(no_to), weight));
                            ShortestPath.nodes.get(no_to).adjacencies.add(new Edge(ShortestPath.nodes.get(no_from), weight));

                            if(isMainRoute.equals("Yes")){
                                mMap.addPolyline(new MapUtil().createPolyline(coordinates, width+4, "#C0C0C0"));
                                mMap.addPolyline(new MapUtil().createPolyline(coordinates, width, "#ffffff").zIndex(1));
                            }
                            else{
                                mMap.addPolyline(new MapUtil().createPolyline(coordinates, width, "#80FF00"));
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

    private void drawRoute(String from, String to) throws JSONException {

        for(Polyline route: mPolylineRoute){
            route.remove();
        }

        mPolylineRoute.clear();

        ShortestPath.computePaths(ShortestPath.nodes.get(MapUtil.location_numbers.get(from)));
        List<Vertex> path = ShortestPath.getShortestPathTo(ShortestPath.nodes.get(MapUtil.location_numbers.get(to)));

        System.out.println(path.toString());

        for(int i=0; i<path.size()-1; i++){
            int x = MapUtil.location_numbers.get(path.get(i).toString());
            int y = MapUtil.location_numbers.get(path.get(i+1).toString());
            String c = MapUtil.routes[x][y];
            System.out.println(c);
            Polyline m = mMap.addPolyline(new MapUtil().createPolyline(c, 9, "#FF0000").zIndex(2));
            mPolylineRoute.add(m);
        }
    }

    private void setMarker(Marker m, String loc){
        try {
            LatLng latLng = MapUtil.location_coordinates.get(loc);
            m.setPosition(latLng);
            m.setTitle(loc);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        catch(IllegalArgumentException e){
            Toast.makeText(this, "Invalid location", Toast.LENGTH_LONG).show();
        }
    }

    private void HideSoftKeyboard(View mainLayout){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }

}
