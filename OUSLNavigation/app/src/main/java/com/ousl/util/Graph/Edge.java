package com.ousl.util.Graph;

public class Edge {
    public final Vertex target;
    public final double weight;

    public Edge(Vertex argTarget, double argWeight){
        target = argTarget;
        weight = argWeight;
    }
}
