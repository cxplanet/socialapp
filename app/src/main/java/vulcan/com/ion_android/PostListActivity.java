package vulcan.com.ion_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import vulcan.com.ion_android.net.OauthJsonObjectRequest;
import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/4/14.
 */
public class PostListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postslist);
        Button b = (Button)findViewById(R.id.fetch_posts_button);
        b.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                fetchPosts();
            }
        });
    }

    protected void fetchPosts()
    {
        String postsUrl = SessionMgr.buildUrl(SessionMgr.YOPPS_FOR_CTA);
        Toast.makeText(this, "Fetching posts", Toast.LENGTH_SHORT).show();

        OauthJsonObjectRequest req = new OauthJsonObjectRequest(postsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostListActivity", response.toString());
                        Toast.makeText(PostListActivity.this, "Successfully fetched posts", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String err;
                if (error.networkResponse.statusCode == 401)
                    err = "this request requires authorization";
                else err = error.getLocalizedMessage();
                Toast.makeText(PostListActivity.this, "An error occured while fetching posts: " + err, Toast.LENGTH_LONG).show();
            }
        });
        SessionMgr.getInstance().mRequestQueue.add(req);
    }

}
