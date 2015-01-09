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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/1/14.
 * NOTE: Currently using ion for file uploads, as Volley is not designed for large files.
 */
public class VideoPostActivity extends BaseActivity {
    private EditText mTitleInput;
    private EditText mDescInput;
    private String mCurrImagename;
    private ImageView mSelectedImg;
    private Button mImagePicker;
    private Button mUploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        mTitleInput = (EditText)findViewById(R.id.post_title);
        mDescInput = (EditText)findViewById(R.id.post_description);

        mUploadButton = (Button)findViewById(R.id.post_data_button);
        mUploadButton.setText("Create Post");

        mSelectedImg = (ImageView)findViewById(R.id.upload_image);
        mSelectedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        mUploadButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
            Toast.makeText(VideoPostActivity.this, "Uploading post", Toast.LENGTH_SHORT).show();
            try {
                uploadVideo("todo", "to-do");
            } catch (IOException ioe)
            {

            }
            }
        });
    }

    private void uploadVideo(String videoPath, String uploadUrl) throws IOException {

        // TODO Consider stashing an instance
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uploadUrl);
        httpPost.addHeader("Authorization", SessionMgr.getInstance().getAuthorizationToken());

        FileBody filebody = new FileBody(new File(videoPath));
        StringBody title = new StringBody("Filename: " + videoPath, ContentType.TEXT_PLAIN);
        StringBody description = new StringBody("This is a description of the video", ContentType.TEXT_PLAIN);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("videoFile", filebody);
        builder.addPart("title", title);
        builder.addPart("description", description);
        httpPost.setEntity(builder.build());

        Log.d("VideoPostActivity", "executing request " + httpPost.getRequestLine());
        HttpResponse response = httpClient.execute( httpPost );
        HttpEntity resEntity = response.getEntity( );
        if (resEntity != null) {
            resEntity.consumeContent( );
        }

        httpClient.getConnectionManager().shutdown( );
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
