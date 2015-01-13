package vulcan.com.ion_android;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.nio.charset.MalformedInputException;
import java.util.HashMap;
import java.util.Map;

import vulcan.com.ion_android.net.AuthListener;
import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/15/14.
 */
public class SignupFragment extends BaseFragment {

    private EditText mUserName;
    private EditText mPasswd;
    private EditText mEmail;
    private EditText mDisplayName;
    private Button mSignupButton;
    private boolean isValidSignupData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_signup, container, false);

        mUserName = (EditText)view.findViewById(R.id.input_username);
        // convenience behavior to dismiss keyboard
        mPasswd = (EditText)view.findViewById(R.id.input_password);
        mPasswd.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)) {

                }
                return false;
            }
        });
        mEmail = (EditText)view.findViewById(R.id.input_email);
        mDisplayName = (EditText)view.findViewById(R.id.input_display_name);

        mSignupButton = (Button)view.findViewById(R.id.signup_button);
        mSignupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                signupUser();
            }
        });

        return view;
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
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        String signupUrl = SessionMgr.buildUrl(SessionMgr.SIGNUP);
        final JSONObject signupData = buildJsonDataForSignup();
        Toast.makeText(getActivity(), "Registering user", Toast.LENGTH_SHORT).show();

        JsonObjectRequest req = new JsonObjectRequest(signupUrl, signupData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display JSON response
                        Toast.makeText(getActivity(), "Signup successful!", Toast.LENGTH_LONG).show();
                        try {
                            String user = signupData.getString("username");
                            String passwd = signupData.getString("password");
                            SessionMgr.getInstance().signinUser(user, passwd, (AuthListener)getActivity());
                        } catch (Exception e)
                        {
                            Log.d("Signup", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err;
                if (error.networkResponse.statusCode == 409)
                    err = "the username or email already exists";
                else err = error.getLocalizedMessage();
                Toast.makeText(getActivity(), "An error occured while signing up: " + err, Toast.LENGTH_LONG).show();
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
