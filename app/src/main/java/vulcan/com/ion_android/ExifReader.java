package vulcan.com.ion_android;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by jayl on 12/17/14.
 */
public class ExifReader {

    private ExifInterface mImageMetadata;

    private ExifReader() { ; }

    public ExifReader(String filePath) throws IOException {
        mImageMetadata = new ExifInterface(filePath);
    }

    // todo
    public String buildJsonData()
    {
        return "";
    }


}
