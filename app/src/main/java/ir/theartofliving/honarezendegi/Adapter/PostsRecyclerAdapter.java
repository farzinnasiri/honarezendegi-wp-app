package ir.theartofliving.honarezendegi.Adapter;

import android.content.Context;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.meness.Library.HtmlTextView.HtmlResImageGetter;
import io.github.meness.Library.HtmlTextView.HtmlTextView;
import io.github.meness.Library.Utils.Utility;
import ir.theartofliving.honarezendegi.Activities.DetailPostActivity;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Post;
import ir.theartofliving.honarezendegi.R;
import maes.tech.intentanim.CustomIntent;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class PostsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private ArrayList<Post> postItems;
    private Context context;
    private final int VIEW_TYPE_POST_ITEM = 0;
    private final int VIEW_TYPE_LOADING_ITEM = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private View.OnClickListener mOnClickListener;

    private boolean isLoading;
    private boolean isFail;

    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;


    public PostsRecyclerAdapter(Context context, ArrayList<Post> postItems, RecyclerView mRecyclerView)
    {
        this.context = context;
        this.postItems = postItems;


        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold))
                {
                    if (mOnLoadMoreListener != null)
                    {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                    isFail = false;
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return postItems == null ? 0 : postItems.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setOnRetryListener(View.OnClickListener mOnClickListener)
    {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (postItems.get(position) == null)
            return VIEW_TYPE_LOADING_ITEM;
        else
            return VIEW_TYPE_POST_ITEM;
    }

    public void setLoaded(boolean loaded)
    {
        isLoading = loaded;
    }

    public boolean isLoading()
    {
        return this.isLoading;
    }

    public boolean isFail()
    {
        return isFail;
    }

    public void setFail(boolean fail)
    {
        isFail = fail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_POST_ITEM)
        {
            View view = G.mLayoutInflater.inflate(R.layout.item_post, parent, false);
            return new postViewHolder(view);
        }
        else if (viewType == VIEW_TYPE_LOADING_ITEM)
        {
            View view = G.mLayoutInflater.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition)
    {

        if (holder instanceof postViewHolder)
        {
            final postViewHolder mPostViewHolder = (postViewHolder) holder;
            Post post = postItems.get(listPosition);
            mPostViewHolder.txtPostTitle.setText(post.getTitle().toString());

            String text = post.getExcerpt().toString();
            mPostViewHolder.txtPostExcerpt.setHtml(text, new HtmlResImageGetter(mPostViewHolder.txtPostExcerpt));

            Glide.with(this.context).load(post.getAuthorAvatarUrl()).placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar).into(mPostViewHolder.imgAuthorAvatarPic);

            mPostViewHolder.txtPostDate.setText(Utility.getDateDuration(post.getDate()));

            mPostViewHolder.txtAuthorName.setText(post.getAuthorName());

            mPostViewHolder.crdHolder.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(PostsRecyclerAdapter.this.context, DetailPostActivity.class);
                    intent.putExtra("id", postItems.get(listPosition).getId()+"");
                    intent.putExtra("title", postItems.get(listPosition).getTitle().toString());
                    intent.putExtra("content", postItems.get(listPosition).getContent().toString());
                    context.startActivity(intent);
                    CustomIntent.customType(context,"left-to-right");
                }
            });

        }
        else if (holder instanceof LoadingViewHolder)
        {
            LoadingViewHolder mLoadingViewHolder = (LoadingViewHolder) holder;
            mLoadingViewHolder.btnRetry.setTypeface(G.face);
            if (isFail == true)
            {
                mLoadingViewHolder.btnRetry.setVisibility(View.VISIBLE);
                mLoadingViewHolder.progressBar.setVisibility(View.GONE);
                mLoadingViewHolder.btnRetry.setOnClickListener(this.mOnClickListener);
            }
            else
            {
                mLoadingViewHolder.btnRetry.setVisibility(View.GONE);
                mLoadingViewHolder.progressBar.setVisibility(View.VISIBLE);
            }
        }

    }

    class postViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtPostTitle;
        private HtmlTextView txtPostExcerpt;
        private TextView txtPostDate;
        private TextView txtAuthorName;
        private CircleImageView imgAuthorAvatarPic;
        private CardView crdHolder;

        public postViewHolder(View itemView)
        {
            super(itemView);
            txtPostTitle = (TextView) itemView.findViewById(R.id.itemTxtPostTitle);
            txtPostExcerpt = (HtmlTextView) itemView.findViewById(R.id.itemTxtPostExcerpt);
            txtPostDate = (TextView) itemView.findViewById(R.id.itemTxtPostDate);
            txtAuthorName = (TextView) itemView.findViewById(R.id.itemTxtAuthorName);
            imgAuthorAvatarPic = (CircleImageView) itemView.findViewById(R.id.itemImgAuthorAvatarPic);
            crdHolder = (CardView) itemView.findViewById(R.id.itemCrdPostHolder);

            txtPostTitle.setTypeface(G.faceBold);
            txtPostDate.setTypeface(G.face);
            txtPostExcerpt.setTypeface(G.face);
            txtAuthorName.setTypeface(G.face);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder
    {
        private MaterialProgressBar progressBar;
        private Button btnRetry;

        public LoadingViewHolder(View itemView)
        {
            super(itemView);
            progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progressBar1);
            btnRetry = (Button)itemView.findViewById(R.id.btnRetry);
        }
    }
}