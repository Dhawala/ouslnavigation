package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

public class LocationRequest extends StringRequest {

    public LocationRequest(Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.GET, Constants.URL_LOCATIONS_API, responseListner, errorListner);
    }

}
