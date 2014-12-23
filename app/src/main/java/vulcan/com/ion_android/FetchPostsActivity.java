package vulcan.com.ion_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private String mPagingUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postslist);
        mPostList = new ArrayList<Post>();
        mListView = (ListView) findViewById(R.id.posts_list);
        mAdapter = new PostListAdapter(this, mPostList);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new EndlessScrollListener());

        fetchPosts();
    }

    protected int currPostCount()
    {
        return mPostList.size();
    }

    protected void fetchPosts()
    {
        String postsUrl;

        if (mPagingUrl == null)
            postsUrl = SessionMgr.buildUrl(SessionMgr.YOPPS_FOR_CTA);
        else
            postsUrl = mPagingUrl;


        //Toast.makeText(this, "Fetching posts", Toast.LENGTH_SHORT).show();

        OauthJsonObjectRequest req = new OauthJsonObjectRequest(postsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("PostListActivity", response.toString());
                        List<Post> posts = Post.buildPostList(response);

                        if (posts.size() > 0)
                            mPostList.addAll(posts);
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

    public class EndlessScrollListener implements AbsListView.OnScrollListener {
        // how many entries earlier to start loading next page
        private int visibleThreshold = 3;
        private int currentPage = 0;
        private int previousTotal = 0;
        private boolean loading = true;

        public EndlessScrollListener() {
        }
        public EndlessScrollListener(int visibleThreshold) {
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
