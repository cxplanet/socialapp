package vulcan.com.ion_android.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/19/14.
 */
public class Post implements Serializable
{
    public String mUgcId;
    public String mThumbnailUrl;
    public String mTitle;
    public String mDesc;
    public String mComments;

    public static Post buildPost(JSONObject post) throws JSONException
    {
        Post p = new Post();
        String ugcId = post.getString("id");
        p.mThumbnailUrl = SessionMgr.buildUrl(String.format("/yopp/%s/picture?client_id=1", ugcId));
        p.mTitle = post.getString("title");
        p.mDesc = post.getString("description");
        int commentCount = post.getInt("comment_count");
        p.mComments = (commentCount > 0) ? String.format("Comments: %s", commentCount) : "";

       // p.mComments = post.getString("comments");

        return p;
    }
}
