package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.activities.FindUsersActivity;
import com.example.myapplication.activities.UserInfoActivity;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private static final String TAG = "UserAdapter";
    private final List<User> userList;
    private List<User> userListFull;
    private final Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        userListFull = new ArrayList<>(userList);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<User> filteredList = new ArrayList<>();
                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(userListFull);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (User user : userListFull) {
                        if (user.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(user);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userList.clear();
                userList.addAll((List<User>) filterResults.values);
                notifyDataSetChanged();
            }
        };
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
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("user", userList.get(getAdapterPosition()));
                    FindUsersActivity findUsersActivity = (FindUsersActivity) context;
                    findUsersActivity.startActivity(intent);
                }
            });

        }
    }
}
