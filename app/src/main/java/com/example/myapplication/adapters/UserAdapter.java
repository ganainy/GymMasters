package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements Filterable {
    private static final String TAG = "UserAdapter";
    private final List<User> userList;
    private List<User> userListFull;
    private final Context context;
    private long sumRatings;
    private int sumRaters;

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
        userViewHolder.nameTextView.setText(currentUser.getName());
        downloadAndShowUserImage(userViewHolder, currentUser.getPhoto());

        getRatingAndFollowers(userViewHolder, i);


    }


    private void getRatingAndFollowers(final UserViewHolder userViewHolder, int i) {

        //show followers count
        FirebaseDatabase.getInstance().getReference("users").child(userList.get(i).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("followersUID")) {
                    userViewHolder.followersTextView.setText(dataSnapshot.child("followersUID").getChildrenCount() + " Followers");
                } else {
                    userViewHolder.followersTextView.setText("0 Followers");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //show avg rating on rating bar
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users").child(userList.get(i).getId());
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Ratings")) {
                    users.child("Ratings").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            sumRatings = 0;
                            sumRaters = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                sumRatings += (long) ds.getValue();
                                sumRaters++;
                            }
                            userViewHolder.ratingBar.setRating(sumRatings / sumRaters);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    userViewHolder.ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //


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
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("user", userList.get(getAdapterPosition()));
                    FindUsersActivity findUsersActivity = (FindUsersActivity) context;
                    findUsersActivity.startActivity(intent);
                }
            });

        }
    }
}
