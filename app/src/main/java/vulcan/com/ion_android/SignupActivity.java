package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vulcan.com.ion_android.net.SessionMgr;
import vulcan.com.ion_android.net.OauthJsonObjectRequest;

/**
 * Created by jayl on 12/3/14.
 */
public class SignupActivity extends BaseActivity {

    private EditText mUserName;
    private EditText mPasswd;
    private EditText mEmail;
    private EditText mDisplayName;
    private Button mSignupButton;
    private boolean isValidSignupData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUserName = (EditText)findViewById(R.id.input_username);
        mPasswd = (EditText)findViewById(R.id.input_password);
        mEmail = (EditText)findViewById(R.id.input_email);
        mDisplayName = (EditText)findViewById(R.id.input_display_name);

        mSignupButton = (Button)findViewById(R.id.signup_button);
        mSignupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                signupUser();
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
        if (id == R.id.action_settings) {
            return true;
        }

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

        if (validationMap.size() == 4)
            isValid = true;

        return isValid;
    }

    protected void signupUser()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        String signupUrl = SessionMgr.buildUrl(SessionMgr.SIGNUP);
        JSONObject signupData = buildJsonDataForSignup();
        Toast.makeText(this, "Registering user", Toast.LENGTH_SHORT).show();

        JsonObjectRequest req = new JsonObjectRequest(signupUrl, signupData,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // display JSON response
                    Toast.makeText(SignupActivity.this, "Successfully signed up user", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String err;
                    if (error.networkResponse.statusCode == 409)
                        err = "the username or email already exists";
                    else err = error.getLocalizedMessage();
                    Toast.makeText(SignupActivity.this, "An error occured while signing up: " + err, Toast.LENGTH_LONG).show();
                }
            });
        SessionMgr.getInstance().mRequestQueue.add(req);
    }

    private JSONObject buildJsonDataForSignup()
    {
        Map<String, String> data = new HashMap<String, String>();
        if (mEmail.getText().toString().length() > 0)
            data.put("email", mEmail.getText().toString());
        if (mDisplayName.getText().toString().length() > 0)
            data.put("name", mDisplayName.getText().toString());
        if (mUserName.getText().toString().length() > 0)
            data.put("username", mUserName.getText().toString());
        if (mPasswd.getText().toString().length() > 0)
            data.put("password", mPasswd.getText().toString());

        JSONObject jsonObj = new JSONObject(data);

        return jsonObj;
    }
}
