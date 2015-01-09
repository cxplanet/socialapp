package vulcan.com.ion_android;

import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;

/**
 * Created by jayl on 12/3/14.
 */
public class AppUtils {

    public enum MEDIA_TYPE
    {
        PNG, JPEG, GIF, MP3, MP4, WAV
    }

    public static MEDIA_TYPE getFileMediaType(String fileName)
    {
        return MEDIA_TYPE.PNG;
    }


}
