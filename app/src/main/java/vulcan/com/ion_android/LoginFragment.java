package vulcan.com.ion_android;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vulcan.com.ion_android.net.AuthListener;

/**
 * Created by jayl on 12/15/14.
 */
public class LoginFragment extends Fragment implements AuthListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_login, container, false);
        return view;
    }

    @Override
    public void onAuthenticationSucceeded() {

    }

    @Override
    public void onAuthenticationFailed(String failureMsg) {

    }
}
