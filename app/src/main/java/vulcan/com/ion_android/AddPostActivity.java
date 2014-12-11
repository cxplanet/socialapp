package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by jayl on 12/4/14.
 */
public class AddPostActivity extends BaseActivity
{
    private Button mPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mPostButton = (Button)findViewById(R.id.post_data_button);
    }

}
