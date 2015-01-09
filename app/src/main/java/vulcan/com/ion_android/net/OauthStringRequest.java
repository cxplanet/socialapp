package vulcan.com.ion_android.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jayl on 12/8/14.
 */
public class OauthStringRequest extends StringRequest {

    public OauthStringRequest(int method, String url, Listener<String> listener,
                         ErrorListener errorListener)
    {
        super(method, url, listener, errorListener);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", SessionMgr.getInstance().getAuthorizationToken());
        return headers;
    }

}

