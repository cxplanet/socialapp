package vulcan.com.ion_android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import vulcan.com.ion_android.SocialApp;

/**
 * Created by jayl on 11/17/14.
 */
public class SessionMgr {

    private final static SessionMgr theInstance = new SessionMgr();

    private String mCurrAuthToken;
    private String mReauthToken;
    private String mAuthDataHeader;
    private SharedPreferences mPrefs;


    private static final String API_BASE_URL = "http://bernard.fayve.com";

    public static final String SIGNUP = "/person/signup?client_id=1";
    public static final String OAUTH_LOGIN = "/oauth2/token?client_id=1";

    public static RequestQueue mRequestQueue;

    private SessionMgr()
    {
        Context ctx = SocialApp.getInstance().getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(ctx);
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

    public String getAuthorizationData()
    {
        return mCurrAuthToken;
    }
}
