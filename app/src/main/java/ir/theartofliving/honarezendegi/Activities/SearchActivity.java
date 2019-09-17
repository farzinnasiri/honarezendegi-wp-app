package ir.theartofliving.honarezendegi.Activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import ir.theartofliving.honarezendegi.Adapter.OnLoadMoreListener;
import ir.theartofliving.honarezendegi.Adapter.PostsRecyclerAdapter;
import ir.theartofliving.honarezendegi.AppSingleton;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Post;
import ir.theartofliving.honarezendegi.R;
import maes.tech.intentanim.CustomIntent;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class SearchActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    private ImageView imgBack;
    private EditText edtSearch;
    private ImageView imgClear;
    private MaterialProgressBar progressBar;
    private RecyclerView rcyclrPostItems;

    int page = 1;
    private int loadItemIndex = 0;
    PostsRecyclerAdapter mRecyclerAdapter;
    ArrayList<Post> postItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        imgClear = (ImageView) findViewById(R.id.imgClear);
        progressBar = (MaterialProgressBar) findViewById(R.id.progressBar);
        rcyclrPostItems = (RecyclerView) findViewById(R.id.rcyclrPostItems);

        progressBar.setVisibility(View.GONE);
        imgClear.setVisibility(View.GONE);
        edtSearch.setTypeface(G.face);

        rcyclrPostItems.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        mRecyclerAdapter = new PostsRecyclerAdapter(SearchActivity.this, postItems, rcyclrPostItems);
        rcyclrPostItems.setAdapter(mRecyclerAdapter);

        edtSearch.setHint(getIntent().getStringExtra("hint"));

        imgClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edtSearch.setText("");
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (!edtSearch.getText().toString().equals(""))
                    imgClear.setVisibility(View.VISIBLE);
                else
                    imgClear.setVisibility(View.GONE);
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    View view = SearchActivity.this.getCurrentFocus();
                    if (view != null)
                    {
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    postItems.clear();
                    mRecyclerAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);
                    page = 1;
                    searchRequest(edtSearch.getText().toString());

                    return true;
                }

                return false;
            }
        });

        mRecyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener()
        {
            @Override
            public void onLoadMore()
            {
                if (postItems.size() / 10 == page)
                {
                    page++;
                    searchRequest(edtSearch.getText().toString());
                    postItems.add(null);
                    loadItemIndex = postItems.size() - 1;
                    mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                }
            }
        });

        mRecyclerAdapter.setOnRetryListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postItems.remove(loadItemIndex);
                mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                mRecyclerAdapter.setFail(false);
                postItems.add(null);
                loadItemIndex = postItems.size() - 1;
                mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                searchRequest(edtSearch.getText().toString());
            }
        });
    }

    private void searchRequest(String textToFind)
    {
        String url = "http://honarzendegi.com/wp-json/wp/v2/posts?search=" + textToFind + "&&per_page=10" + "&&page=" + page;
        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                if (page != 1)
                {
                    postItems.remove(loadItemIndex);
                    mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                }

                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<Post>>()
                {
                }.getType();
                Collection<Post> postCollection = gson.fromJson(response.toString(), collectionType);
                postItems.addAll(postCollection);
                mRecyclerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mRecyclerAdapter.setLoaded(false);
                if (postCollection.size() != 0)
                    Toast.makeText(getApplicationContext(), postCollection.size() + " مورد جدید یافت شد", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "موردی یافت نشد", Toast.LENGTH_LONG).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressBar.setVisibility(View.GONE);
                if (page == 1)
                    Toast.makeText(getApplicationContext(), "اتصال به اینترنت برقرار نیست", Toast.LENGTH_LONG).show();
                else
                {
                    postItems.remove(loadItemIndex);
                    mRecyclerAdapter.notifyItemRemoved(loadItemIndex);
                    mRecyclerAdapter.setFail(true);
                    postItems.add(null);
                    loadItemIndex = postItems.size() - 1;
                    mRecyclerAdapter.notifyItemInserted(loadItemIndex);
                }

            }
        };

        JsonArrayRequest request = new JsonArrayRequest(url, listener, errorListener);
        AppSingleton.getInstance(SearchActivity.this).addToRequestQueue(request);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this,"fadein-to-fadeout");
    }
}