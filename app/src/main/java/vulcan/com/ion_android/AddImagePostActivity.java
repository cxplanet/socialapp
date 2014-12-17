package vulcan.com.ion_android;

import android.os.Bundle;
import android.widget.Button;

/**
 * Created by jayl on 12/4/14.
 */
public class AddImagePostActivity extends BaseActivity
{
    private Button mPostButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);
        mPostButton = (Button)findViewById(R.id.post_data_button);
    }

}
