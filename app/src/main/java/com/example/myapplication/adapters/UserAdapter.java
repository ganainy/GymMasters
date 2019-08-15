package com.example.myapplication.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private static final String TAG = "UserAdapter";
    private final List<User> userList;
    private final Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
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
        userViewHolder.followersTextView.setText(currentUser.getFollowers() + " Followers");
        userViewHolder.nameTextView.setText(currentUser.getName());
        if (currentUser.getRating().equals("-1")) {
            userViewHolder.ratingBar.setVisibility(View.GONE);
        } else {
            userViewHolder.ratingBar.setRating(Integer.valueOf(currentUser.getRating()));
            //change star color to blue
            LayerDrawable stars = (LayerDrawable) userViewHolder.ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
        }
        downloadAndShowUserImage(userViewHolder, currentUser.getPhoto());

    }

    private void downloadAndShowUserImage(final UserViewHolder userViewHolder, String photo) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/").child(photo);

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).into(userViewHolder.userImageView);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "workout photo download failed " + e.getMessage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userImageView)
        CircleImageView userImageView;

        @BindView(R.id.nameTextView)
        TextView nameTextView;

        @BindView(R.id.followersTextView)
        TextView followersTextView;

        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo open user full profile with his workouts and exercises
                }
            });

        }
    }
}
