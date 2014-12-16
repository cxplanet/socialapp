package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;

/**
 * Created by jayl on 12/9/14.
 */
public class BaseActivity extends Activity implements Validator.ValidationListener {

    private Validator mTextValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextValidator = new Validator(this);
        mTextValidator.setValidationListener(this);
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
                intent = new Intent(this, PostListActivity.class);
                startActivity(intent);
                break;
            case R.id.action_image_upload:
                intent = new Intent(this, FileUploadActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showToastMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onValidationSucceeded() {
        showToastMessage("Validation Succeeded");
    }

    @Override
    public void onValidationFailed(View failedView, Rule<?> failedRule) {
        showToastMessage("Validation Failed");
    }
}
