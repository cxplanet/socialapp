package vulcan.com.ion_android;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

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

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/1/14.
 * NOTE: Currently using ion for file uploads, as Volley is not designed for large files.
 */
public class NewPostActivity extends BaseActivity {
    @NotEmpty(message="Title is required")
    private EditText mTitleInput;
    @NotEmpty(message="Description is required")
    private EditText mDescInput;
    private String mCurrImagename;
    private ImageView mSelectedImg;
    private Button mImagePicker;
    private Button mUploadButton;

    final int IMAGE_CAPTURE_REQUEST = 100001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleInput = (EditText)findViewById(R.id.post_title);
        mDescInput = (EditText)findViewById(R.id.post_description);

        mUploadButton = (Button)findViewById(R.id.post_data_button);
        mUploadButton.setText("Create Post");
        mUploadButton.setVisibility(View.INVISIBLE);

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
                dismissKeyboard();
                mInputValidator.validate();
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

    private void dismissKeyboard()
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void openImageIntent() {

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }
        // Filesystem.
        // XXX not sure if we should grab both INTERNAL_CONTENT_URI as well as EXTERNAL_CONTENT_URI
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, IMAGE_CAPTURE_REQUEST);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if(resultCode == RESULT_OK)
//        {
//            if(requestCode == IMAGE_CAPTURE_REQUEST)
//            {
//                final boolean isCamera;
//                if(data == null)
//                {
//                    isCamera = true;
//                }
//                else
//                {
//                    final String action = data.getAction();
//                    if(action == null)
//                    {
//                        isCamera = false;
//                    }
//                    else
//                    {
//                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    }
//                }
//
//                Uri selectedImageUri;
//                if(isCamera)
//                {
//                    //selectedImageUri = outputFileUri;
//                }
//                else
//                {
//                    selectedImageUri = data == null ? null : data.getData();
//                }
//            }
//        }
//    }

    private void uploadPost()
    {
        if (mCurrImagename.equalsIgnoreCase(""))
        {
            Toast.makeText(NewPostActivity.this, "Unable to upload file", Toast.LENGTH_SHORT).show();
            return;
        }
        final File fileToUpload = new File(mCurrImagename);
        final String title = mTitleInput.getText().toString();
        final String desc = mDescInput.getText().toString();
        Ion.with(this)
                .load(SessionMgr.buildUrl(SessionMgr.IMAGE_POST))
                .setHeader("Authorization", SessionMgr.getInstance().getAuthorizationToken())
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
                .setMultipartFile("picture" +
                        "", "image/jpeg", fileToUpload)
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

                        if (e != null) {
                            Log.d("ImagePostActivity error", e.getLocalizedMessage());
                            Toast.makeText(NewPostActivity.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            Log.d("ImagePostActivity error", result.toString());
                            Toast.makeText(NewPostActivity.this, "Upload complete", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NewPostActivity.this, PostsActivity.class));
                            finish();
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.actionbar_attach_photo:
                openImageIntent();
                break;

            case R.id.actionbar_save_post:
                dismissKeyboard();
                mInputValidator.validate();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();

            mCurrImagename = getImagePath(targetUri);
            Log.d(this.getClass().getName(), "Image selection: " + mCurrImagename);
            Bitmap bitmap;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                mSelectedImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getImagePath(Uri uri)
    {
        String imgPath = "";

        // grrr... if its the google photo app, and you have cloud syncing enabled, after
        // some time your local file reference goes away, and you will need to download the
        // image from the cloud.
        if (uri.toString().startsWith("content://com.google.android.apps.photos.content"))
        {
//            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
//            File file = new File(extStorageDirectory, Constants.IMG_DOWNLOAD_FILENAME);
//
//            imgPath = file.getAbsolutePath();
//
//            downloadImageToLocalTemp(uri, file); // possible thread race here
        }
        else
        {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
            cursor.close();

            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return imgPath;
    }

    private void downloadImageToLocalTemp(Uri uri, File downloadFile) {
        if (downloadFile.exists()) {
            String filePath = downloadFile.getAbsolutePath();
            downloadFile.delete();
            downloadFile = new File(filePath);
        }
        try {

            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri ,"r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            InputStream inputStream = new FileInputStream(fileDescriptor);
            BufferedInputStream reader = new BufferedInputStream(inputStream);

            File tempFile = createTempImageFile();

            BufferedOutputStream outStream = new BufferedOutputStream(
                    new FileOutputStream(tempFile));
            byte[] buf = new byte[2048];
            int len;
            while ((len = reader.read(buf)) > 0) {
                outStream.write(buf, 0, len);
            }
        }
        catch(Exception e)
        {

        }
    }

    private File createTempImageFile() {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();

        File file = new File(extStorageDirectory, "temp_img.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "temp_img.png");
        }
        return file;
    }

    public void onValidationSucceeded() {
        Toast.makeText(NewPostActivity.this, "Uploading post", Toast.LENGTH_SHORT).show();
        uploadPost();
    }

    public void onValidationFailed(List<ValidationError> errors) {
        StringBuilder errorMsg = new StringBuilder();
        for (ValidationError error : errors) {
            TextView tv = (TextView) error.getView();
            if (tv != null) {
                tv.setHint(error.getCollatedErrorMessage(this));
                tv.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
                errorMsg.append(error.getCollatedErrorMessage(this));
            }
        }
        Log.d("New Post", "Post Failed: " + errorMsg);
    }

}
