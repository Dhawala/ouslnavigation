package com.ousl.util;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapUtil {

    public static ArrayList<String> locations = new ArrayList<>();
    public static HashMap<String, LatLng> location_coordinates = new HashMap<>();
    public static ArrayList<Polygon> buildings = new ArrayList<>();
    public static HashMap<String, Integer> location_numbers = new HashMap<>();
    public static String routes[][];

    public PolylineOptions createPolyline(String coordinates, float w, String c){
        PolylineOptions poly = new PolylineOptions();

        String points [] = coordinates.split(":");
        for(int i=0; i<points.length; i++){
            String latlan [] = points[i].split(",");
            float lat = Float.valueOf(latlan[0]);
            float lan = Float.valueOf(latlan[1]);
            poly.add(new LatLng(lat, lan));
        }

        poly.width(w);
        poly.color(Color.parseColor(c));
        poly.geodesic(true);
        return poly;
    }

    public PolygonOptions createPolygon(String coordinates){
        PolygonOptions poly = new PolygonOptions();

        String points [] = coordinates.split(":");
        for(int i=0; i<points.length; i++){
            String latlan [] = points[i].split(",");
            float lat = Float.valueOf(latlan[0]);
            float lan = Float.valueOf(latlan[1]);
            poly.add(new LatLng(lat, lan));
        }

        poly.strokeColor(Color.GRAY);
        poly.strokeWidth(2);
        poly.fillColor(Color.LTGRAY);

        return poly;
    }
}
