package ganainy.dev.gymmasters.ui.specificExercise.youtubeFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import ganainy.dev.gymmasters.R;
import ganainy.dev.gymmasters.ui.specificExercise.ExerciseFragment;
import ganainy.dev.gymmasters.utils.ApplicationViewModelFactory;

public class YoutubeFragment extends Fragment {

    private static final String NAME = "name";
    public static final String YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query=";

    private YoutubeViewModel mViewModel;

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView youTubePlayerView;

    public static YoutubeFragment newInstance(String exerciseName) {
        YoutubeFragment youtubeFragment = new YoutubeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NAME, exerciseName);
        youtubeFragment.setArguments(bundle);
        return youtubeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.youtube_fragment, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();

        /*add youtube player as fragment life cycle observer to control be released when fragment is destroyed*/
        getViewLifecycleOwner().getLifecycle().addObserver(youTubePlayerView);


        if (getArguments().getString(NAME)!=null){
            mViewModel.getExerciseVideoId(getArguments().getString(NAME));
        }

        /*if we got video id which is related to this exercise play it in youtube player*/
        mViewModel.getVideoIdLiveData().observe(getViewLifecycleOwner(), videoId -> {
            if (videoId!=null) {
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener(){
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(videoId, mViewModel.getVideoCurrentSecond());
                    }

                    @Override
                    public void onCurrentSecond(@NotNull YouTubePlayer youTubePlayer, float second) {
                        mViewModel.setVideoCurrentSecond(second);
                        super.onCurrentSecond(youTubePlayer, second);
                    }
                });
            } else {
                showOpenInYoutubeAlertDialog();
            }
        });

    }

    private void initViewModel() {
        ApplicationViewModelFactory applicationViewModelFactory=new ApplicationViewModelFactory(getActivity().getApplication());
        mViewModel =new ViewModelProvider(this,applicationViewModelFactory).get(YoutubeViewModel.class);
    }


    private void showOpenInYoutubeAlertDialog() {
        new AlertDialog.Builder(requireActivity())
                .setMessage(R.string.in_app_play_failed).setTitle(R.string.error_playing_video)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_SEARCH_URL + mViewModel.getExerciseName()));
                    startActivity(intent);
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                }).create().show();
    }

}