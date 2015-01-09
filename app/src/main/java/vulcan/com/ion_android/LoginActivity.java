package vulcan.com.ion_android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/3/14.
 */
public class LoginActivity extends BaseActivity {

    private EditText mUserName;
    private EditText mPasswd;
    private Button mSigninButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserName = (EditText)findViewById(R.id.login_username);
        mPasswd = (EditText)findViewById(R.id.login_password);

        mSigninButton = (Button)findViewById(R.id.login_button);
        mSigninButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                signinUser();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected boolean validateData(Map<String, String> validationMap)
    {
        boolean isValid = false;

        if (validationMap.size() == 2)
            isValid = true;

        return isValid;
    }

    protected void signinUser()
    {
        // dismiss keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        String signinUrl = SessionMgr.buildUrl(SessionMgr.OAUTH_LOGIN);

        StringRequest req = new StringRequest(Request.Method.POST, signinUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response!= null) {
                            try {
                                JSONObject tokenData = new JSONObject(response);
                                SessionMgr.getInstance().updateAuthData(tokenData);
                                showToastMessage("Successfully signed in");

                            } catch (JSONException e) {
                                // TODO
                                e.printStackTrace();
                                showToastMessage("Error while signing in: " + e.getLocalizedMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showToastMessage("Unable to log in: " + error.getLocalizedMessage());
                    }
                })
        {

            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mUserName.getText().toString());
                params.put("password", mPasswd.getText().toString());
                params.put("grant_type", "password");
                params.put("client_id", "1");
                return params;
            };
        };
        SessionMgr.getInstance().mRequestQueue.add(req);
    }

    private JSONObject buildJsonDataForSignup()
    {
        Map<String, String> data = new HashMap<String, String>();
        if (mUserName.getText().toString().length() > 0)
            data.put("username", mUserName.getText().toString());
        if (mPasswd.getText().toString().length() > 0)
            data.put("password", mPasswd.getText().toString());

        JSONObject jsonObj = new JSONObject(data);

        return jsonObj;
    }

}
