package ganainy.dev.gymmasters.ui.specificExercise;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Timer;
import java.util.TimerTask;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.utils.AuthUtils;

import static ganainy.dev.gymmasters.ui.muscle.MuscleViewModel.EXERCISES;

public class ExerciseViewModel extends AndroidViewModel {

    private static final String TAG = "ExerciseViewModel";
    public static final String NAME = "name";
    public static final String ID = "id";

    private Timer timer;

    private Exercise mExercise;
    private Boolean mIsLoggedUserExercise;

    private Drawable mFirstDrawable;
    private Drawable mSecondDrawable;
    private Boolean mIsShowingFirstImage=false;


    private MutableLiveData<ExerciseSelectedImage> exerciseSelectedImageLiveData =new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedUserExerciseLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isExerciseDeletedSuccessfullyLiveData = new MutableLiveData<>();

    public ExerciseViewModel(@NonNull Application application) {
        super(application);
    }


    public void switchToExercisePhotos() {
         timer = new Timer();
        //switch exercise photos every 1.5 seconds
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                    if (!mIsShowingFirstImage) {
                        mIsShowingFirstImage = true;
                        exerciseSelectedImageLiveData.postValue(ExerciseSelectedImage.IMAGE_ONE);
                    } else {
                        mIsShowingFirstImage = false;
                        exerciseSelectedImageLiveData.postValue(ExerciseSelectedImage.IMAGE_TWO);
                    }
                }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1500);
    }

    /**check if this exercise is created by logged in user to allow delete option*/
    public void isLoggedUserExercise(){
        if (mIsLoggedUserExercise!=null)return;

        mIsLoggedUserExercise= mExercise.getCreatorId().equals(AuthUtils.getLoggedUserId(getApplication()));
        isLoggedUserExerciseLiveData.setValue(mIsLoggedUserExercise);
    }

    /**delete exercise photos from storage then delete exercise from real time db*/
    void deleteExercise() {

        final String exerciseId = mExercise.getId();
        final String previewPhoto1 = mExercise.getPreviewPhotoOneUrl();
        final String previewPhoto2 = mExercise.getPreviewPhotoTwoUrl();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(EXERCISES);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChild(ID) && ds.child(ID).getValue().toString().equals(exerciseId)) {
                        String nodeKey = ds.getKey();
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(previewPhoto1);
                        storageReference.delete().addOnSuccessListener(aVoid -> {

                            StorageReference storageReference2 = FirebaseStorage.getInstance().getReferenceFromUrl(previewPhoto2);
                            storageReference2.delete().addOnSuccessListener(aVoid2 -> {

                                databaseReference.child(nodeKey).setValue(null);
                                isExerciseDeletedSuccessfullyLiveData.setValue(true);
                            }).addOnFailureListener(exception -> {
                                // Error
                                isExerciseDeletedSuccessfullyLiveData.setValue(false);
                            });
                        }).addOnFailureListener(exception -> {
                            // Error
                            isExerciseDeletedSuccessfullyLiveData.setValue(false);
                        });

                        return;
                    }
                }
                isExerciseDeletedSuccessfullyLiveData.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });
    }


    public LiveData<Boolean> getIsExerciseDeletedSuccessfullyLiveData() {
        return isExerciseDeletedSuccessfullyLiveData;
    }

    public void loadExercisePhotos() {

        if (mFirstDrawable !=null && mSecondDrawable !=null){
            return;
        }

       Glide.with(getApplication()).load(mExercise.getPreviewPhotoOneUrl()).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                mFirstDrawable = resource;
               Glide.with(getApplication()).load(mExercise.getPreviewPhotoTwoUrl()).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mSecondDrawable = resource;
                        switchToExercisePhotos();
                        return false;
                    }
                }).submit();
                return false;
            }
        }).submit();
    }

    //region getters & setters


    public Exercise getExercise() {
        return mExercise;
    }

    public void setExercise(Exercise exercise) {
        this.mExercise = exercise;
    }

    public LiveData<ExerciseSelectedImage> getExerciseSelectedImageLiveData() {
        return exerciseSelectedImageLiveData;
    }

    public LiveData<Boolean> getIsLoggedUserExerciseLiveData() {
        return isLoggedUserExerciseLiveData;
    }

    public Drawable getFirstDrawable() {
        return mFirstDrawable;
    }

    public Drawable getSecondDrawable() {
        return mSecondDrawable;
    }

    //endregion


    @Override
    protected void onCleared() {
        super.onCleared();
        if (timer!=null)
        timer.cancel();
    }
}