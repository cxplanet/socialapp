package vulcan.com.ion_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;


public class MainActivity extends Activity {

    private Button mLoginButton;
    private Button mSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // decide whether to login or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mLoginButton = (Button)findViewById(R.id.button_login);

        mSignupButton = (Button)findViewById(R.id.button_signin);
        mSignupButton.setTextColor(getResources().getColor(R.color.light_grey));

        loadAuthFragment();
    }

    public void handleAuthButtonClick(Button button) {
        //TODO - bold the currently selected button, and dim the other
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

        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.action_signup:
                intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;
            case R.id.action_login:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_new_post:
                intent = new Intent(this, AddPostActivity.class);
                startActivity(intent);
                break;
            case R.id.action_post_list:
                intent = new Intent(this, FileUploadActivity.class);
                startActivity(intent);
                break;
            case R.id.action_image_upload:
                intent = new Intent(this, FileUploadActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAuthFragment()
    {
        Fragment authFragment = new LoginFragment()
        FragmentManager fragMgr = getFragmentManager();
        fragMgr.beginTransaction()
                .replace(R.id.auth_container, authFragment)
                .addToBackStack("auth")
                .commit();
    }

    public boolean onMenuItemClick(MenuItem item) {

        return true;
    }

}
