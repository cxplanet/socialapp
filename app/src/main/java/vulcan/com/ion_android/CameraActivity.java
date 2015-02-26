package vulcan.com.ion_android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vulcan.com.ion_android.common.Constants;

/**
 * Created by jayl on 2/24/15.
 */
public class CameraActivity extends BaseCameraActivity {

    private final String TAG = this.getClass().getName();
    
    private CameraPreview mPreview;
    private LayoutInflater mOverlayInflater;

    // callback to process image
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = createTempImageFile();
            if (pictureFile == null){
                Log.d(TAG, "Error creating image, check storage permissions");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                Log.d(TAG, "Successfully created image");
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        mCamera = initializeCamera();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        LinearLayout preview = (LinearLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Image capture button
        ImageButton captureButton = (ImageButton) findViewById(R.id.button_image_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
        // Image browse button
        ImageButton browseButton = (ImageButton) findViewById(R.id.browse_image_button);
        browseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        openGalleryIntent();
                    }
                }
        );
        // Video capture button
        ImageButton videoButton = (ImageButton) findViewById(R.id.button_video_capture);
        videoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        Toast.makeText(CameraActivity.this, "Video not implemented yet", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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


    private void getMostRecentPicture()
    {
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        final Cursor cursor = getContentResolver().query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + "DESC" );

        if (cursor.moveToFirst()) {
            final ImageView imageView = (ImageView) findViewById(R.id.browse_image_button);
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                imageView.setImageBitmap(bm);
            }
        }
    }

    private void openGalleryIntent() {

        final Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Constants.IMAGE_CAPTURE_REQUEST);
    }

//    /**
//     * A safe way to get an instance of the Camera object.
//     * BTW, Camera is deprecated, but Camera2 only works on
//     * Lollipop devices, so we'll stick with this for the forseeable future
//     * */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//        }
//        return c; // returns null if camera is unavailable
//    }
//
//    private Camera initializeCamera() {
//
//        // Create an instance of Camera
//        Camera camera = getCameraInstance();
//
//        if (camera == null)
//            return null;
//
//        Camera.Parameters params = camera.getParameters();
//
//        List list = params.getSupportedPictureSizes();
//        if(list != null) {
//            android.hardware.Camera.Size size = null;
//            Iterator iterator = list.iterator();
//            do {
//                if(!iterator.hasNext())
//                    break;
//                android.hardware.Camera.Size size1 = (android.hardware.Camera.Size)iterator.next();
//                if(Math.abs(3F * ((float)size1.width / 4F) - (float)size1.height) < 0.1F * (float)size1.width && (size == null || size1.height > size.height && size1.width < 3000))
//                    size = size1;
//            } while(true);
//            if(size != null)
//                params.setPictureSize(size.width, size.height);
//            else
//                Log.e("CameraSettings", "No supported picture size found");
//        }
//
//        return camera;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Get the Camera instance as the activity achieves full user focus
//        if (mCamera == null) {
//            mCamera = initializeCamera(); // Local method to handle camera init
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();  // Always call the superclass method first
//
//        // Release the Camera because we don't need it when paused
//        // and other activities might need to use it.
//        if (mCamera != null) {
//            mCamera.release();
//            mCamera = null;
//        }
//    }

}