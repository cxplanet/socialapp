package vulcan.com.ion_android;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.FileNotFoundException;

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/1/14.
 * NOTE: Currently using ion for file uploads, as Volley is not designed for large files.
 */
public class ImagePostActivity extends BaseActivity {
    private EditText mTitleInput;
    private EditText mDescInput;
    private String mCurrImagename;
    private ImageView mSelectedImg;
    private Button mImagePicker;
    private Button mUploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);

        mTitleInput = (EditText)findViewById(R.id.post_title);
        mDescInput = (EditText)findViewById(R.id.post_description);

        mUploadButton = (Button)findViewById(R.id.post_data_button);
        mUploadButton.setText("Create Post");

        mSelectedImg = (ImageView)findViewById(R.id.upload_image);
        mSelectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        mUploadButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Toast.makeText(ImagePostActivity.this, "Uploading post", Toast.LENGTH_SHORT).show();
                uploadPost();
            }
        });

    }

    private void togglePostControls(boolean hidePostFields)
    {
        if (hidePostFields)
        {
            mUploadButton.setVisibility(View.INVISIBLE);
        }
    }

    private void uploadPost()
    {
        final File fileToUpload = new File(mCurrImagename);
        final String title = mTitleInput.getText().toString();
        final String desc = mDescInput.getText().toString();
        Ion.with(this)
                .load(SessionMgr.buildUrl(SessionMgr.IMAGE_POST))
                .setHeader("Authorization", SessionMgr.getInstance().getAuthorizationData())
                .uploadProgressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        // Displays the progress bar for the first time.
//                        mNotifyManager.notify(notificationId, mBuilder.build());
//                        mBuilder.setProgress((int) total, (int) uploaded, false);
                    }
                })
                .setTimeout(3 * 60 * 1000)
                .setMultipartParameter("title", title)
                .setMultipartParameter("description", desc)
                .setMultipartFile("upload", "image/jpeg", fileToUpload)
                .asJsonObject()
                        // run a callback on completion
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // When the loop is finished, updates the notification
//                        mBuilder.setContentText("Upload complete")
//                                // Removes the progress bar
//                                .setProgress(0, 0, false);
//                        mNotifyManager.notify(notificationId, mBuilder.build());
                        Log.d("ImagePostActivity", result.toString());
                        if (e != null) {
                            Toast.makeText(ImagePostActivity.this, "Error uploading file", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(ImagePostActivity.this, "File upload complete", Toast.LENGTH_LONG).show();
                    }
                });
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

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            mCurrImagename = getImagePath(targetUri);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                mSelectedImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
