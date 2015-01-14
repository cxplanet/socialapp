package vulcan.com.ion_android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vulcan.com.ion_android.common.AppUtils;
import vulcan.com.ion_android.data.Post;
import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/1/14.
 * NOTE: Currently using ion for file uploads, as Volley is not designed for large files.
 */
public class PostDetailActivity extends Activity {

    private TextView mPostDesc;
    private ImageView mPostImg;
    private ProgressBar mDownloadProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mPostDesc = (EditText)findViewById(R.id.post_description);
        mPostImg = (ImageView)findViewById(R.id.post_image);
        mDownloadProgressBar = (ProgressBar)findViewById(R.id.image_progress);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Post p = (Post) bundle.getSerializable("post");
            this.setTitle(p.mTitle);
            mPostDesc.setText(p.mDesc);
            mDownloadProgressBar.setVisibility(View.VISIBLE);

            ImageLoader imgLoader = SessionMgr.getInstance().mImageLoader;
            imgLoader.get(p.mThumbnailUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response != null) {
                        Bitmap bitmap = response.getBitmap();
                        if (bitmap != null) {
                            mDownloadProgressBar.setVisibility(View.INVISIBLE);
                            mPostImg.setImageBitmap(bitmap);
                        }
                    }
                }

                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(PostDetailActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                    mDownloadProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    private void togglePostControls(boolean hidePostFields)
    {
        // todo - user who owns post should be able to edit it
    }

    private void showImageDetail() {
        // todo - show image detail
    }


    private void editPost()
    {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
