package com.ousl.dbo;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ousl.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    private Map<String, String> params;

    public LoginRequest(String username, String password, Response.Listener<String> responseListner, Response.ErrorListener errorListner){
        super(Method.POST, Constants.URL_LOGIN_API, responseListner, errorListner);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
