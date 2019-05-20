package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

public class BuildingRequest extends StringRequest {

    public BuildingRequest(Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.GET, Constants.URL_BUILDINGS_API, responseListner, errorListner);
    }

}
