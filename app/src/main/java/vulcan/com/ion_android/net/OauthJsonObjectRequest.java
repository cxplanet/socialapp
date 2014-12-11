package vulcan.com.ion_android.net;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OauthJsonObjectRequest extends JsonObjectRequest{

    public OauthJsonObjectRequest(String url, JSONObject jsonObj, Listener<JSONObject> listener, ErrorListener errorListener) {
        super(url, jsonObj, listener, errorListener);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", SessionMgr.getInstance().getAuthorizationData());
        return headers;
    }
}