package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 1/6/15.
 * A non UI based activity that decides what activity to launch, based
 * on the current auth state.
 */
public class DispatcherActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class<?> activityClass;

        // do we have a valid token?S
        //

        SessionMgr.AuthState currState = SessionMgr.getInstance().getAuthState();

        if(currState == SessionMgr.AuthState.VALID_TOKEN) {
            startActivity(new Intent(this, PostsActivity.class));
        }
        else {
            activityClass = MainActivity.class;
        }

        startActivity(new Intent(this, PostsActivity.class));

    }


}
