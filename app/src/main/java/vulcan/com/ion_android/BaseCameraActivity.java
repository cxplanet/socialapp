package vulcan.com.ion_android;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by jayl on 2/25/15.
 */
public class BaseCameraActivity extends Activity {

    protected Camera mCamera;

    /**
     * A safe way to get an instance of the Camera object.
     * BTW, Camera is deprecated, but Camera2 only works on
     * Lollipop devices, so we'll stick with this for the forseeable future
     * */
    protected Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    protected Camera initializeCamera() {

        // Create an instance of Camera
        Camera camera = getCameraInstance();

        if (camera == null)
            return null;

        Camera.Parameters params = camera.getParameters();

        List list = params.getSupportedPictureSizes();
        if(list != null) {
            android.hardware.Camera.Size size = null;
            Iterator iterator = list.iterator();
            do {
                if(!iterator.hasNext())
                    break;
                android.hardware.Camera.Size size1 = (android.hardware.Camera.Size)iterator.next();
                if(Math.abs(3F * ((float)size1.width / 4F) - (float)size1.height) < 0.1F * (float)size1.width && (size == null || size1.height > size.height && size1.width < 3000))
                    size = size1;
            } while(true);
            if(size != null)
                params.setPictureSize(size.width, size.height);
            else
                Log.e("CameraSettings", "No supported picture size found");
        }

        return camera;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get the Camera instance as the activity achieves full user focus
        if (mCamera == null) {
            mCamera = initializeCamera(); // Local method to handle camera init
        }
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


}
