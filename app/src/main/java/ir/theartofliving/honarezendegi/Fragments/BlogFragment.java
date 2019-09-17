package ir.theartofliving.honarezendegi.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

import ir.theartofliving.honarezendegi.Adapter.OnLoadMoreListener;
import ir.theartofliving.honarezendegi.Adapter.PostsRecyclerAdapter;
import ir.theartofliving.honarezendegi.AppSingleton;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Post;
import ir.theartofliving.honarezendegi.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class BlogFragment extends Fragment {

    private MaterialProgressBar progressBar;
    private ArrayList<Post> postItems = new ArrayList<>();
    private RecyclerView rcyclrPostItems;
    private PostsRecyclerAdapter mRecyclerAdapter;

    private String filterData = "";

    //Fail Ui
    private Button btnRetry;
    private LinearLayout lnrContainerFail;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int page = 1;
    private int loadItemIndex = -1;
    private boolean prograssBarVisible;

    public BlogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        findViews(view);
        setDefaultData();
        getBlogContent();
        return view;
    }

    private void findViews(View view) {
        progressBar = (MaterialProgressBar) view.findViewById(R.id.progressBar);
        rcyclrPostItems = (RecyclerView) view.findViewById(R.id.rcyclrPostItems);

        btnRetry = (Button) view.findViewById(R.id.btnRetry);
        lnrContainerFail = (LinearLayout) view.findViewById(R.id.lnrContainerFail);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
    }

    private void setDefaultData() {
        rcyclrPostItems.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerAdapter = new PostsRecyclerAdapter(getContext(), postItems, rcyclrPostItems);
        rcyclrPostItems.setAdapter(mRecyclerAdapter);

        btnRetry.setTypeface(G.face);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadBlogContent();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.swipePrograssBarColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRecyclerAdapter.isLoading() || mRecyclerAdapter.isFail()) {
                    swipeRefreshLayout.setRefreshing(false);
                } else if(!prograssBarVisible &&!mRecyclerAdapter.isLoading()){
                    reloadBlogContent();
                    if (!mRecyclerAdapter.isFail()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 2000);
                    }
                    swipeRefreshLayout.setRefreshing(false);


                }

            }
        });

        mRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (postItems.size() / 10 == page) {
                    page++;
                    postItems.add(null);
                    loadItemIndex = postItems.size() - 1;
                    mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                    getBlogContent();
                }
            }
        });

        mRecyclerAdapter.setOnRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postItems.remove(loadItemIndex);
                mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                mRecyclerAdapter.setFail(false);
                postItems.add(null);
                loadItemIndex = postItems.size() - 1;
                mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                getBlogContent();
            }
        });

    }

    private void getBlogContent() {
        if (page == 1) {
            progressBar.setVisibility(View.VISIBLE);
            prograssBarVisible = true;
            rcyclrPostItems.setVisibility(View.VISIBLE);
            lnrContainerFail.setVisibility(View.GONE);
        }

        String url = "http://honarzendegi.com/wp-json/wp/v2/posts?per_page=10&&page=" + page
                + this.filterData;

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                if (page != 1) {
                    postItems.remove(loadItemIndex);
                    mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                }

                Gson gson = new Gson();
                Post[] posts = gson.fromJson(response.toString(), Post[].class);
                for (int i = 0; i < posts.length; i++)
                    postItems.add(posts[i]);
                mRecyclerAdapter.notifyDataSetChanged();
                mRecyclerAdapter.setLoaded(false);
                prograssBarVisible = false;
                progressBar.setVisibility(View.GONE);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (page == 1) {
                    rcyclrPostItems.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    prograssBarVisible = false;
                    lnrContainerFail.setVisibility(View.VISIBLE);
                } else {
                    postItems.remove(loadItemIndex);
                    mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                    mRecyclerAdapter.setFail(true);
                    postItems.add(null);
                    loadItemIndex = postItems.size() - 1;
                    mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                }
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        AppSingleton.getInstance(G.mContext).addToRequestQueue(request);
    }

    public void getFilterData(String data) {
        this.filterData = "&&" + data;
        postItems.clear();
        mRecyclerAdapter.notifyDataSetChanged();
        page = 1;
        progressBar.setVisibility(View.VISIBLE);
        prograssBarVisible = true;
        getBlogContent();
    }

    private void reloadBlogContent() {
        postItems.clear();
        mRecyclerAdapter.notifyDataSetChanged();
        getBlogContent();

    }
}