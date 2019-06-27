package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class UpdateStudentDetailsRequest extends StringRequest {

    private Map<String, String> params;

    public UpdateStudentDetailsRequest(String column, String email, String sno, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_UPDATESTUDENTDETAILS_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("column", column);
        params.put("email", email);
        params.put("sno", sno);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
