package vulcan.com.ion_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vulcan.com.ion_android.data.Post;
import vulcan.com.ion_android.data.PostListAdapter;
import vulcan.com.ion_android.net.OauthJsonObjectRequest;
import vulcan.com.ion_android.net.SessionMgr;

/**
 * Created by jayl on 12/4/14.
 */
public class FetchPostsActivity extends BaseActivity {

    private ListView mListView;
    private PostListAdapter mAdapter;
    private ArrayList<Post>mPostList;
    private ProgressDialog mProgDialog;
    private String mNextPageUrl;
    private boolean mHasMorePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postslist);
        mPostList = new ArrayList<Post>();
        mListView = (ListView) findViewById(R.id.posts_list);
        mAdapter = new PostListAdapter(this, mPostList);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new InfiniteScrollListener());
        mHasMorePosts = true;

        fetchPosts();
    }

    protected int currPostCount()
    {
        return mPostList.size();
    }

    protected void fetchPosts()
    {
        if(mHasMorePosts)
        {
            String postsUrl;

            if (mNextPageUrl == null)
                mNextPageUrl = SessionMgr.buildUrl(SessionMgr.YOPPS_FOR_CTA);

            OauthJsonObjectRequest req = new OauthJsonObjectRequest(mNextPageUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("PostListActivity", response.toString());
                            parseResponse(response);

                            mAdapter.notifyDataSetChanged();
                            // Toast.makeText(PostListActivity.this, "Successfully fetched posts", Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String err;
                    if (error.networkResponse.statusCode == 401)
                        err = "this request requires authorization";
                    else err = error.getLocalizedMessage();
                    Toast.makeText(FetchPostsActivity.this, "An error occurred while fetching posts: " + err, Toast.LENGTH_LONG).show();
                }
            });
            SessionMgr.getInstance().mRequestQueue.add(req);
        }
    }

    private void parseResponse(JSONObject response)
    {
        // we assume the data is found in a dictionary call 'data'
        try {
            if (response.getJSONObject("data") != null)
            {
                JSONObject jsonData = response.getJSONObject("data");
                String nextPage = jsonData.getJSONObject("paging").getString("start");
                if (nextPage.equalsIgnoreCase(mNextPageUrl))
                {
                    mHasMorePosts = false;
                }
                else
                {
                    mNextPageUrl = nextPage;
                }
                JSONArray postObjs = jsonData.getJSONArray("items");
                if(postObjs.length() > 0) {
                    ArrayList<Post> posts = new ArrayList<Post>();
                    for (int i = 0; i < postObjs.length(); i++) {
                        Post p = Post.buildPost(postObjs.getJSONObject(i));
                        posts.add(p);
                    }
                    if (posts.size() > 0)
                        mPostList.addAll(posts);
                }
            }
            else
            {
                Log.d("Post.buildPosts", "No data dictionary found in response");
            }
        }
        catch(JSONException jsonEx)
        {
            Log.d("FetchPostsActivity.parseResponse", "Parse error: " + jsonEx.getLocalizedMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanup();
    }

    private void cleanup()
    {
        if (mProgDialog != null)
        {
            mProgDialog.dismiss();
            mProgDialog = null;
        }
        mPostList.clear();
    }

    public class InfiniteScrollListener implements AbsListView.OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 3;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public InfiniteScrollListener() {
        }
        public InfiniteScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                fetchPosts();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }


        public int getCurrentPage() {
            return currentPage;
        }
    }

}
