package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

public class RouteRequest extends StringRequest {

    public RouteRequest(Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.GET, Constants.URL_ROUTES_API, responseListner, errorListner);
    }

}
