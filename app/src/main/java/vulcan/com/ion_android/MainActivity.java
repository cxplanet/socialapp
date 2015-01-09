package vulcan.com.ion_android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import vulcan.com.ion_android.net.AuthListener;
import vulcan.com.ion_android.net.SessionMgr;


public class MainActivity extends BaseActivity implements AuthListener{

    private Button mLoginButton;
    private Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO - put this in a dispatcher activity
        SessionMgr.AuthState currState = SessionMgr.getInstance().getAuthState();

        switch (currState) {
            case VALID_TOKEN:
                startActivity(new Intent(this, PostsActivity.class));
                break;
            case EXPIRED_TOKEN:
                SessionMgr.getInstance().reauthUser(this);
            case NO_TOKEN:
                showUserLanding();
                showLoginFragment();
                break;
        }
    }

    private void showUserLanding() {
        setContentView(R.layout.activity_main);
        // decide whether to login or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mSignupButton = (Button) findViewById(R.id.button_signin);
        mSignupButton.setTextColor(getResources().getColor(R.color.light_grey));
        mSignupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showSignup();
            }
        });

        mLoginButton = (Button) findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoginFragment();
            }
        });
    }

    public void handleAuthButtonClick(View button) {
        Button b = (Button)button;
        if (b == mLoginButton)
        {
            showLoginFragment();
        } else if (b == mSignupButton)
        {
            showSignup();
        }
    }

    private void showLoginFragment()
    {
        mLoginButton.setTextColor(getResources().getColor(R.color.black));
        mSignupButton.setTextColor(getResources().getColor(R.color.light_grey));
        Fragment authFragment = new LoginFragment();
        loadAuthFragment(authFragment);
    }

    private void showSignup()
    {
        mLoginButton.setTextColor(getResources().getColor(R.color.light_grey));
        mSignupButton.setTextColor(getResources().getColor(R.color.black));
        Fragment authFragment = new SignupFragment();
        loadAuthFragment(authFragment);
    }

    private void loadAuthFragment(Fragment authFragment)
    {
        FragmentManager fragMgr = getFragmentManager();
        fragMgr.beginTransaction()
                .replace(R.id.auth_container, authFragment)
                .addToBackStack("auth")
                .commit();
    }

    public boolean onMenuItemClick(MenuItem item) {

        return true;
    }

    @Override
    public void onAuthenticationSucceeded() {
        startActivity(new Intent(this, PostsActivity.class));
    }

    @Override
    public void onAuthenticationFailed(String failureMsg) {
        showUserLanding();
    }
}
