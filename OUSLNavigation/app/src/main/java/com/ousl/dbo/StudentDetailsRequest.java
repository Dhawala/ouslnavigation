package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class StudentDetailsRequest extends StringRequest {

    private Map<String, String> params;

    public StudentDetailsRequest(String sno, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_STUDENTDETAILS_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("sno", sno);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
