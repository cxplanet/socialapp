package vulcan.com.ion_android.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import vulcan.com.ion_android.common.Constants;
import vulcan.com.ion_android.SocialApp;

/**
 * Created by jayl on 11/17/14.
 */
public class SessionMgr {

    public enum AuthState {
        NO_TOKEN, VALID_TOKEN, EXPIRED_TOKEN
    }

    // todo - maintain a list of listeners, as we are likely to
    private final static SessionMgr theInstance = new SessionMgr();

    private String mAuthDataHeader;
    private String mCurrAuthToken; //
    private String mReauthToken;
    private SharedPreferences mPrefs;


    private static final String TTL_EXPIRY_KEY ="ttl_prefs_key";
    private static final String REAUTH_TOKEN_KEY ="reauth_prefs_key";
    private static final String AUTH_TOKEN_KEY ="access_token";
    private static final String AUTH_TOKEN_TYPE_KEY="auth_token_type_key";

    private static final String API_BASE_URL = "https://bernard.fayve.com";

    public static final String SIGNUP = "/person/signup?client_id=1";
    public static final String OAUTH_LOGIN = "/oauth2/token?client_id=1";
    public static final String IMAGE_POST = "/cta/" + Constants.YOPP_TOPIC_ID + "/yopps?client_id=1";
    public static final String YOPPS_FOR_CTA = "/cta/" + Constants.YOPP_TOPIC_ID + "/yopps/"; //?client_id=1";

    public static RequestQueue mRequestQueue;
    public static ImageLoader mImageLoader;

    private Context mContext;

    private SessionMgr()
    {
        mContext = SocialApp.getInstance().getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(mContext);
        mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        initAuthData();
    }

    public static SessionMgr getInstance()
    {
        return theInstance;
    }

    public static String buildUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    private void initAuthData()
    {
        mCurrAuthToken = mPrefs.getString(AUTH_TOKEN_KEY, "");
        mReauthToken = mPrefs.getString(REAUTH_TOKEN_KEY, "");
        if (mCurrAuthToken.length() > 0) {
            String tokenType = mPrefs.getString(AUTH_TOKEN_TYPE_KEY, "");
            mAuthDataHeader = String.format("%s %s", tokenType, mCurrAuthToken);
        }
    }
    public void updateAuthData(JSONObject tokenData) {
        try {
            // todo - validate data
            String authToken = tokenData.getString("access_token");
            String tokenType = tokenData.getString("token_type");
            String reauthToken = tokenData.getString("refresh_token");
            int tokenTTL = tokenData.getInt("expires_in");
            mAuthDataHeader = String.format("%s %s", tokenType, authToken);

            // todo robustify me
            // store the newly acquired data
            Date now = new Date();
            long tokenExpiry = tokenTTL * Constants.MILLIS_TO_SEC + now.getTime();

            SharedPreferences.Editor editor = mPrefs.edit();

            editor.putString(AUTH_TOKEN_KEY, authToken);
            editor.putString(AUTH_TOKEN_TYPE_KEY, tokenType);
            editor.putString(REAUTH_TOKEN_KEY, reauthToken);
            editor.putLong(TTL_EXPIRY_KEY, tokenExpiry);
            editor.commit();

        } catch (JSONException e) {
            // TODO
            e.printStackTrace();
        }
    }

    public AuthState getAuthState()
    {
        AuthState currState = AuthState.NO_TOKEN;

        // 1) do we have a token at all - if we don't pass this we return NO_TOKEN
        String token = mPrefs.getString(AUTH_TOKEN_KEY, "");
        if(token.length() > 0)
        {
            // 2) if we do, is it still valid from the client's perspective
            long expiry = mPrefs.getLong(TTL_EXPIRY_KEY, 0L);
            Date now = new Date();
            if (expiry > now.getTime())
            {
                currState = AuthState.VALID_TOKEN;
            }
            else
            {
                // 3) let the client know its expired, so they can use the refresh token
                String reAuthToken = mPrefs.getString(REAUTH_TOKEN_KEY, "");
                if (reAuthToken.length() > 0)
                    currState = AuthState.EXPIRED_TOKEN;
            }
        }
        return currState;
    }

    private ImageLoader buildOauthImageLoader(RequestQueue reqQueue){
        ImageLoader imageLoader = new ImageLoader(reqQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                }) {
            @Override
            protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight, final String cacheKey) {
                //return super.makeImageRequest(requestUrl, maxWidth, maxHeight, cacheKey);

                return new ImageRequest(requestUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        onGetImageSuccess(cacheKey, response);
                    }
                }, maxWidth, maxHeight,
                        Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onGetImageError(cacheKey, error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Authorization", SessionMgr.getInstance().mAuthDataHeader);
                        return params;
                    }
                };
            }
        };

        return imageLoader;
    }

    // much like login, with a different payload. TODO - refactor the request
    public void reauthUser(final AuthListener listener)
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
                                Toast.makeText(mContext, "Successfully reauthenticated", Toast.LENGTH_SHORT).show();
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
                        ///TODO - nothing fancy here, we'll just have a dispatcher listen
                        // for it and throw up the login screen
                        listener.onAuthenticationFailed(error.getLocalizedMessage());
                    }
                })
        {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("refresh_token", mReauthToken);
                params.put("grant_type", "refresh_token");
                params.put("client_id", "1");
                params.put("client_secret", "foobar");
                return params;
            };
        };
        SessionMgr.getInstance().mRequestQueue.add(req);
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
                                //Toast.makeText(mContext, "Successfully signed in", Toast.LENGTH_SHORT).show();
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

    public String getAuthorizationToken()
    {
        return mAuthDataHeader;
    }
}
