package ganainy.dev.gymmasters.ui.findUser;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.utils.MyConstant;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindUsersActivity extends AppCompatActivity {
    private static final String TAG = "FindUsersActivity";
    public NetworkChangeReceiver receiver;
    Boolean bl = true;
    List<User> userList = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private UserAdapter userAdapter;
    private ArrayList<String> followersIdList = new ArrayList<>();
    private List<User> followersList = new ArrayList<>();
    private ArrayList<String> followingIdList = new ArrayList<>();
    private List<User> followingList = new ArrayList<>();

    @BindView(R.id.notFoundTextView)
    TextView notFoundTextView;

    @BindView(R.id.button)
    Button findButton;

    @BindView(R.id.bgImageView)
    ImageView bgImageView;
    private RecyclerView recyclerView;

    @OnClick(R.id.button)
    void loadAllUsersList() {
        loadAllUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);
        ButterKnife.bind(this);

        recyclerView = findViewById(R.id.usersRecycler);



        /**setting up custom toolbar*/
        setSupportActionBar(toolbar);



        checkInternet();
        //this activity called from more than one source so we differ with intent
        if (getIntent().getStringExtra("source").equals("find"))
            loadAllUsers();
        else if (getIntent().getStringExtra("source").equals("followers")) {
            loadFollowers();
            notFoundTextView.setText("No followers yet");
        } else if (getIntent().getStringExtra("source").equals("following")) {
            loadFollowing();
            notFoundTextView.setText("Not following anyone yet");
        }
    }


    private void loadFollowing() {
        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(MyConstant.loggedInUserId);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingIdList.clear();
                if (dataSnapshot.hasChild("followingUID")) {
                    users.child("followingUID").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followingIdList.add(ds.getValue().toString());

                            }
                            getFollowingData();
                            Log.i(TAG, "onDataChange: followingIddddList " + followingIdList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    //loged in user has no ONE FOLLOWING HIM
                    Log.i(TAG, "onDataChange: no following");
                    notFoundTextView.setVisibility(View.VISIBLE);
                    findButton.setVisibility(View.VISIBLE);
                    bgImageView.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

    private void getFollowingData() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        users.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (int i = 0; i < followingIdList.size(); i++) {
                        if (followingIdList.get(i).equals(ds.getKey())) {
                            User user = new User();
                            user.setName(ds.child("name").getValue().toString());
                            if (ds.hasChild("photo"))
                                user.setPhoto(ds.child("photo").getValue().toString());
                            user.setId(ds.child("id").getValue().toString());
                            if (ds.hasChild("about_me"))
                                user.setAbout_me(ds.child("about_me").getValue().toString());
                            if (ds.hasChild("email"))
                                user.setEmail(ds.child("email").getValue().toString());
                            followingList.add(user);
                        }
                    }
                }

                Log.i(TAG, "onDataChange: followingList" + followingList.size());
                setupRecycler("getFollowingData");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

    private void loadFollowers() {

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users").child(MyConstant.loggedInUserId);
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersIdList.clear();
                if (dataSnapshot.hasChild("followersUID")) {
                    users.child("followersUID").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                followersIdList.add(ds.getValue().toString());

                            }
                            getFollowersData();
                            Log.i(TAG, "onDataChange: followersIddddList " + followersIdList.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    //loged in user has no followers
                    Log.i(TAG, "onDataChange: no followers");
                    notFoundTextView.setVisibility(View.VISIBLE);
                    bgImageView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
        });
    }

    private void getFollowersData() {

        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
        users.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followersList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (int i = 0; i < followersIdList.size(); i++) {
                        if (followersIdList.get(i).equals(ds.getKey())) {

                            User user = new User();
                            user.setName(ds.child("name").getValue().toString());
                            if (ds.hasChild("photo"))
                                user.setPhoto(ds.child("photo").getValue().toString());
                            user.setId(ds.child("id").getValue().toString());
                            if (ds.hasChild("about_me"))
                                user.setAbout_me(ds.child("about_me").getValue().toString());
                            if (ds.hasChild("email"))
                                user.setEmail(ds.child("email").getValue().toString());
                            followersList.add(user);
                        }
                    }
                }

                Log.i(TAG, "onDataChange: followersList" + followersList.size());
                setupRecycler("getFollowersData");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

    private void loadAllUsers() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("id").getValue().equals(MyConstant.loggedInUserId)) {
                        //don't show this user in list since it's the logged in user
                    } else {
                        User user = new User();
                        user.setName(ds.child("name").getValue().toString());
                        if (ds.hasChild("photo"))
                            user.setPhoto(ds.child("photo").getValue().toString());
                        user.setId(ds.child("id").getValue().toString());
                        if (ds.hasChild("about_me"))
                            user.setAbout_me(ds.child("about_me").getValue().toString());
                        if (ds.hasChild("email"))
                            user.setEmail(ds.child("email").getValue().toString());
                        userList.add(user);
                    }

                }
                setupRecycler("loadAllUsers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FancyToast.makeText(FindUsersActivity.this, "Check network connection and try again.", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

            }
        });
    }

    private void setupRecycler(String source) {

        findButton.setVisibility(View.GONE);
        notFoundTextView.setVisibility(View.GONE);
        bgImageView.setVisibility(View.GONE);

        if (source.equals("getFollowersData")) {
            userAdapter = new UserAdapter(FindUsersActivity.this, followersList);
        } else if (source.equals("loadAllUsers")) {
            userAdapter = new UserAdapter(FindUsersActivity.this, userList);
        } else if (source.equals("getFollowingData")) {
            userAdapter = new UserAdapter(FindUsersActivity.this, followingList);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FindUsersActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //for filtering
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_users_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        //hide search button from keyboard since it does nothing and we filter on text change
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //so app won't crash if no data in recycler
                if (userAdapter != null)
                    userAdapter.getFilter().filter(s);
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void checkInternet() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(this);
        registerReceiver(receiver, filter);
        bl = receiver.is_connected();
        Log.d("Boolean ", bl.toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        recyclerView.setAdapter(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (userAdapter != null) recyclerView.setAdapter(userAdapter);
    }

    /** handle back press from toolbar
     */
    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        onBackPressed();
    }
}

