package com.ousl.util;

import java.util.ArrayList;

public class Vertex implements Comparable<Vertex> {
    public final String name;
    public ArrayList<Edge> adjacencies = new ArrayList<>();
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;

    public Vertex(String argName){
        name = argName;
    }

    public String toString(){
        return  name;
    }

    public int compareTo(Vertex other){
        return  Double.compare(minDistance, other.minDistance);
    }
}
