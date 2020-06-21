package ganainy.dev.gymmasters.ui.posts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.ui.createExercise.CreateExerciseViewModel;
import ganainy.dev.gymmasters.ui.findUser.FindUsersActivity;
import ganainy.dev.gymmasters.utils.ApplicationViewModelFactory;
import ganainy.dev.gymmasters.utils.NetworkChangeReceiver;

import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ganainy.dev.gymmasters.utils.NetworkUtil;

public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsActivity";
    private NetworkChangeReceiver networkChangeReceiver;
    private PostsViewModel mViewModel;
    private SharedAdapter sharedAdapter;

    @BindView(R.id.notFoundTextView)
    TextView notFoundTextView;
    @BindView(R.id.findUsersButton)
    Button button;
    @BindView(R.id.bgImageView)
    ImageView bgImageView;
    @BindView(R.id.loadingTextView)
    TextView loadingTextView;
    @BindView(R.id.loadingProgressbar)
    ProgressBar loadingProgressbar;

    @BindView(R.id.sharedRv)
     RecyclerView recyclerView;

    @OnClick(R.id.findUsersButton)
    void openFindUsers() {
        Intent i = new Intent(PostsActivity.this, FindUsersActivity.class);
        i.putExtra("source", "find");
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        ButterKnife.bind(this);

        setupRecycler();
        initViewModel();

        mViewModel.getFollowingUid();

        mViewModel.getNetworkStateLiveData().observe(this,networkState->{
            switch (networkState){

                case SUCCESS:
                    //hide loading
                    loadingTextView.setVisibility(View.GONE);
                    loadingProgressbar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    //todo
                    break;
                case LOADING:
                    loadingTextView.setVisibility(View.VISIBLE);
                    loadingProgressbar.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    notFoundTextView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                    bgImageView.setVisibility(View.VISIBLE);
                    loadingTextView.setVisibility(View.GONE);
                    loadingProgressbar.setVisibility(View.GONE);
                    break;
                case STOP_LOADING:
                    break;
            }

        });


        mViewModel.getPostListLiveData().observe(this,posts -> {

            Collections.sort(posts, (s1, s2) -> s2.getDateStamp().compareTo(s1.getDateStamp()));
            sharedAdapter.setData(posts);
            sharedAdapter.notifyDataSetChanged();

        });
    }

    private void initViewModel() {
        ApplicationViewModelFactory applicationViewModelFactory=new ApplicationViewModelFactory(getApplication());
        mViewModel = new ViewModelProvider(this,applicationViewModelFactory).get(PostsViewModel.class);
    }


    private void setupRecycler() {

        //todo open selected exercise
        /* Intent i = new Intent(this,);
                i.putExtra("exercise", currentExercise);
                startActivity(i);*/
         sharedAdapter = new SharedAdapter(getApplicationContext(), new PostCallback() {
            @Override
            public void onExerciseClicked(Exercise exercise, Integer adapterPosition) {
                //todo open selected exercise
               /* Intent i = new Intent(this,);
                i.putExtra("exercise", currentExercise);
                startActivity(i);*/
            }
        });
        //sharedExerciseWorkoutList setdata

        recyclerView.setAdapter(sharedAdapter);


    }



    @Override
    protected void onStop() {
        super.onStop();
        NetworkUtil.unregisterNetworkReceiver(this,networkChangeReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkChangeReceiver = NetworkUtil.registerNetworkReceiver(this);
    }


    /**
     * handle back press from toolbar
     */
    @OnClick(R.id.backArrowImageView)
    public void onViewClicked() {
        onBackPressed();
    }


}
