package vulcan.com.ion_android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vulcan.com.ion_android.Constants;
import vulcan.com.ion_android.SocialApp;

/**
 * Created by jayl on 11/17/14.
 */
public class SessionMgr {

    // todo - maintain a list of listeners, as we are likely to
    private final static SessionMgr theInstance = new SessionMgr();

    private String mCurrAuthToken;
    private String mReauthToken;
    private String mAuthDataHeader;
    private SharedPreferences mPrefs;


    private static final String API_BASE_URL = "http://bernard.fayve.com";

    public static final String SIGNUP = "/person/signup?client_id=1";
    public static final String OAUTH_LOGIN = "/oauth2/token?client_id=1";
    public static final String IMAGE_POST = "/yopps?client_id=1";
    public static final String YOPPS_FOR_CTA = "/cta/" + Constants.YOPP_TOPIC_ID + "/yopps";

    public static RequestQueue mRequestQueue;
    public static ImageLoader mImageLoader;

    private SessionMgr()
    {
        Context ctx = SocialApp.getInstance().getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(ctx);
        mImageLoader = new ImageLoader(this.mRequestQueue,
                new LruBitmapCache());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static SessionMgr getInstance()
    {
        return theInstance;
    }

    public static String buildUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    public void updateAuthData(JSONObject tokenData) {
        try {
            // todo - validate data
            String authToken = tokenData.getString("access_token");
            String tokenType = tokenData.getString("token_type");
            String reauthToken = tokenData.getString("refresh_token");
            int tokenTTL = tokenData.getInt("expires_in");
            mCurrAuthToken = String.format("%s %s", tokenType, authToken);
        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
        }
    }

    public void reauthUser(final AuthListener listener)
    {
        //todo - call the reauth call
    }

    public void signinUser(final String username, final String password, final AuthListener listener)
    {
        String signinUrl = buildUrl(SessionMgr.OAUTH_LOGIN);

        StringRequest req = new StringRequest(Request.Method.POST, signinUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!= null) {
                            try {
                                JSONObject tokenData = new JSONObject(response);
                                updateAuthData(tokenData);
                                //showToastMessage("Successfully signed in");
                                listener.onAuthenticationSucceeded();

                            } catch (JSONException e) {
                                // TODO
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ///showToastMessage("Unable to log in: " + error.getLocalizedMessage());
                        listener.onAuthenticationFailed(error.getLocalizedMessage());
                    }
                })
        {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("grant_type", "password");
                params.put("client_id", "1");
                return params;
            };
        };
        SessionMgr.getInstance().mRequestQueue.add(req);
    }

    public String getAuthorizationData()
    {
        return mCurrAuthToken;
    }
}
