package vulcan.com.ion_android.common;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

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

    public static void dismissKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()) { // verify if the soft keyboard is open
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public static boolean isVideo()
    {
        return false;
    }


}
