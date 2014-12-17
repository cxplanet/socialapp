package vulcan.com.ion_android.net;

import android.view.View;

import com.mobsandgeeks.saripaar.Rule;

/**
 * Created by jayl on 12/15/14.
 */
public interface AuthListener {

    public void onAuthenticationSucceeded();

    public void onAuthenticationFailed(String failureMsg);

}
