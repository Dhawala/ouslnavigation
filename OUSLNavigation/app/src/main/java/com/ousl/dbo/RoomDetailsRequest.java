package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class RoomDetailsRequest extends StringRequest {

    private Map<String, String> params;

    public RoomDetailsRequest(String room, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_ROOMDETAILS_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("room", room);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
