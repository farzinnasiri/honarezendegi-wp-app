package ir.theartofliving.honarezendegi.Adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.meness.Library.HtmlTextView.HtmlResImageGetter;
import io.github.meness.Library.HtmlTextView.HtmlTextView;
import io.github.meness.Library.Utils.Utility;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Comment;
import ir.theartofliving.honarezendegi.R;


public class CommentsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private ArrayList<Comment> commentItems;
    private LayoutInflater mLayoutInflater;


    public CommentsRecyclerAdapter(Context context, ArrayList<Comment> commentItems)
    {
        this.commentItems = commentItems;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getItemCount()
    {
        return commentItems == null ? 0 : commentItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mLayoutInflater.inflate(R.layout.item_coment, parent, false);
        return new commentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition)
    {
        final commentViewHolder mCommentViewHolder = (commentViewHolder) holder;
        Comment comment = commentItems.get(listPosition);

        mCommentViewHolder.txtAuthorName.setText(comment.getAuthor_name());
        mCommentViewHolder.txtCommentDate.setText(Utility.getDateDuration(comment.getDate()));
        String text = comment.getContent().toString();
        mCommentViewHolder.txtComment.setHtml(text,new HtmlResImageGetter(mCommentViewHolder.txtComment));
        Glide.with(G.mContext).load(comment.getAuthor_avatar_urls().getImageUrl()).placeholder(R.drawable.noprofilepic).error(R.drawable.noprofilepic).into(mCommentViewHolder.imgAuthorAvatarPic);

    }

    static class commentViewHolder extends RecyclerView.ViewHolder
    {
        public HtmlTextView txtComment;
        public TextView txtCommentDate;
        public TextView txtAuthorName;
        public CircleImageView imgAuthorAvatarPic;

        public commentViewHolder(View itemView)
        {
            super(itemView);
            txtComment = (HtmlTextView) itemView.findViewById(R.id.txtAuthorComment);
            txtCommentDate = (TextView) itemView.findViewById(R.id.txtAuthorCommentDate);
            txtAuthorName = (TextView) itemView.findViewById(R.id.txtAuthorName);
            imgAuthorAvatarPic = (CircleImageView) itemView.findViewById(R.id.imgAuthorAvatarPic);

            txtCommentDate.setTypeface(G.face);
            txtComment.setTypeface(G.face);
            txtAuthorName.setTypeface(G.face);

        }
    }
}