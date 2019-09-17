package ir.theartofliving.honarezendegi.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.github.meness.Library.HtmlTextView.HtmlHttpImageGetter;
import io.github.meness.Library.HtmlTextView.HtmlTextView;
import ir.theartofliving.honarezendegi.Adapter.CommentsRecyclerAdapter;
import ir.theartofliving.honarezendegi.AppSingleton;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Comment;
import ir.theartofliving.honarezendegi.R;
import maes.tech.intentanim.CustomIntent;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class DetailPostActivity extends AppCompatActivity
{

    private HtmlTextView txtContent;
    private TextView txtTitle;
    private Toolbar toolbar;

    Button btnLoadComments;
    MaterialProgressBar progressLoading;
    RecyclerView recyclerComments;
    String id;

    ArrayList<Comment> commentItems = new ArrayList<>();
    CommentsRecyclerAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_post);

        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtContent = (HtmlTextView) findViewById(R.id.txtContent);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressLoading = (MaterialProgressBar) findViewById(R.id.progressLoading);
        btnLoadComments = (Button) findViewById(R.id.btnShowComments);
        recyclerComments = (RecyclerView) findViewById(R.id.rcyclrComments);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(getIntent().getStringExtra("title"));


        txtTitle.setTypeface(G.faceBold);
        txtContent.setTypeface(G.face);



        id = getIntent().getStringExtra("id");
        txtTitle.setText(getIntent().getStringExtra("title"));
        String text = getIntent().getStringExtra("content");
        txtContent.setHtml(text, new HtmlHttpImageGetter(txtContent));


        progressLoading.setVisibility(View.GONE);
        btnLoadComments.setVisibility(View.VISIBLE);

        btnLoadComments.setTypeface(G.face);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext())
        {
            @Override
            public boolean canScrollVertically()
            {
                return false;
            }
        };
        recyclerComments.setLayoutManager(layoutManager);
        mRecyclerAdapter = new CommentsRecyclerAdapter(DetailPostActivity.this, commentItems);
        recyclerComments.setAdapter(mRecyclerAdapter);

        btnLoadComments.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getComments();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getComments()
    {
        btnLoadComments.setVisibility(View.GONE);
        progressLoading.setVisibility(View.VISIBLE);

        String url = "http://honarzendegi.com/wp-json/wp/v2/comments";

        Response.Listener<String> listener = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                Gson gson = new Gson();
                Comment[] comments = gson.fromJson(response, Comment[].class);
                for (int i = 0; i < comments.length; i++)
                    commentItems.add(comments[i]);
                mRecyclerAdapter.notifyDataSetChanged();
                if (commentItems.size() == 0)
                    Toast.makeText(getApplicationContext(), "کامنتی وجود ندارد", Toast.LENGTH_LONG).show();
                progressLoading.setVisibility(View.GONE);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressLoading.setVisibility(View.GONE);
                btnLoadComments.setVisibility(View.VISIBLE);
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, url, listener, errorListener);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this,"right-to-left");
    }
}