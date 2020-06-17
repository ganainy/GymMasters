package ganainy.dev.gymmasters.ui.findUser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.ui.userInfo.UserInfoActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = "UserAdapter";
    private  List<User> userList;
    private final Context context;
    private UserCallback userCallback;


    public UserAdapter(Context context, UserCallback userCallback) {
        this.context = context;
        this.userCallback = userCallback;
    }

    public void setData(List<User> userList){
        this.userList=userList;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item,
                viewGroup, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        User currentUser = userList.get(i);
        userViewHolder.nameTextView.setText(currentUser.getName());

        if (userList.get(i).getPhoto()!=null) {
            Glide.with(context).load(userList.get(i).getPhoto()).into(userViewHolder.userImageView);
        }

        if (userList.get(i).getFollowers()!=null) {
            userViewHolder.followersTextView.setText(context.getString(R.string.followers_count,userList.get(i).getFollowers()));
        }

        if (userList.get(i).getRating() != null) {
            userViewHolder.ratingBar.setRating(userList.get(i).getRating());
        }

    }



    @Override
    public int getItemCount() {
        return userList==null?0:userList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userImageView)
        CircleImageView userImageView;

        @BindView(R.id.nameEditText)
        TextView nameTextView;

        @BindView(R.id.followersTextView)
        TextView followersTextView;

        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            itemView.setOnClickListener(view -> {
              userCallback.onUserClicked(userList.get(getAdapterPosition()),getAdapterPosition());
            });

        }
    }


}
