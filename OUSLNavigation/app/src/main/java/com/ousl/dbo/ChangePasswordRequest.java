package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordRequest extends StringRequest {

    private Map<String, String> params;

    public ChangePasswordRequest(String password, String sno, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_CHANGEPASSWORD_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("password", password);
        params.put("sno", sno);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}
