package vulcan.com.ion_android;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vulcan.com.ion_android.net.AuthListener;
import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/15/14.
 */
public class LoginFragment extends Fragment implements AuthListener{

    private EditText mUserName;
    private EditText mPasswd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_login, container, false);
        final Button loginButton = (Button) view.findViewById(R.id.login_button);
        mUserName = (EditText)view.findViewById(R.id.login_username);
        mPasswd = (EditText)view.findViewById(R.id.login_password);
        mPasswd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginUser();
                    return true;
                }
                return false;
            }
        });
        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loginUser();
            }
        });
        return view;
    }

    private void loginUser()
    {
        // hide the keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        // todo - validate the user
        String user = mUserName.getText().toString();
        String passwd = mPasswd.getText().toString();

        if (user.length() > 0 && passwd.length() > 0)
        {
            SessionMgr.getInstance().signinUser(user, passwd, this);
        }
    }

    @Override
    public void onAuthenticationSucceeded() {
        //Toast.makeText(getActivity(), "Successfully signed in", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), PostsActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onAuthenticationFailed(String failureMsg) {
        Toast.makeText(getActivity(), "Unable to sign in: " + failureMsg, Toast.LENGTH_SHORT).show();
    }
}
