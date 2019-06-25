package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class RoomRequest extends StringRequest {

    private Map<String, String> params;

    public RoomRequest(String location, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_ROOMS_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("location", location);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
