package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public boolean onMenuItemClick(MenuItem item) {

        return true;
    }

}
