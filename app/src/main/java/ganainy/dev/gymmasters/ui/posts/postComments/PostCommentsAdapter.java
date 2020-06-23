package ganainy.dev.gymmasters.ui.posts.postComments;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Comment;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.posts.SharedAdapter;

public class PostCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<Pair<Comment, User>> userCommentPairList;
    Application app;
    CommentCallback commentCallback;

    public PostCommentsAdapter(Application app, CommentCallback commentCallback) {
        this.app = app;
        this.commentCallback = commentCallback;
    }

    public void setData(List<Pair<Comment, User>> userCommentPairList){
        this.userCommentPairList=userCommentPairList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(app).inflate(R.layout.comment_item, viewGroup, false);
        return new PostCommentsAdapter.PostCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Pair<Comment, User> currentUserCommentPair=userCommentPairList.get(position);
        ((PostCommentsAdapter.PostCommentViewHolder) viewHolder).setUi(currentUserCommentPair);
    }

    @Override
    public int getItemCount() {
        return userCommentPairList==null?0:userCommentPairList.size();
    }

    public class PostCommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.commenterImageView)
        ImageView commenterImageView;

        @BindView(R.id.commenterNameTextView)
        TextView commenterNameTextView;

        @BindView(R.id.commentTextView)
        TextView commentTextView;

        @BindView(R.id.commentDateTextView)
        TextView commentDateTextView;


        public PostCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }

        public void setUi(Pair<Comment, User> currentUserCommentPair) {
            Glide.with(app).load(currentUserCommentPair.second.getPhoto())
                    .apply(new RequestOptions().placeholder(R.drawable.loading_animation).error(R.drawable.anonymous_profile))
                    .circleCrop()
                    .into(commenterImageView);

            commenterNameTextView.setText(currentUserCommentPair.second.getName());

            commentTextView.setText(currentUserCommentPair.first.getText());

            commentDateTextView.setText(new PrettyTime().format(new Date(currentUserCommentPair.first.getDateCreated())));
        }
    }
}
