package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class UpcomingRequest extends StringRequest {

    private Map<String, String> params;

    public UpcomingRequest(String sno, String datefrom, String dateto, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_UPCOMING_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("sno", sno);
        params.put("datefrom", datefrom);
        params.put("dateto", dateto);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
