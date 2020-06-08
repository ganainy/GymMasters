package ganainy.dev.gymmasters.ui;

import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ganainy.dev.gymmasters.model.Exercise;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExerciseActivityViewModel extends ViewModel {
    private static final String TAG = "ExerciseActivityViewMod";

    List<Exercise> exerciseList = new ArrayList<>();
    private MutableLiveData<List<Exercise>> load, load2;

    //load exercises from firebase database
    public LiveData<List<Exercise>> getExercises(String muscle) {

        /**if exerciseList got items no need to download again (for example when i rotate device)*/
        if (exerciseList.size() > 0 && load != null) {
            return load;
        }

        load = new MutableLiveData<>();
        final DatabaseReference exercisesNode = FirebaseDatabase.getInstance().getReference("excercises");
        DatabaseReference myRef = null;

        //get exercises only for the selected muscle by passing it from the exercise activity to this fragment
        switch (muscle) {
            case "triceps": {
                myRef = exercisesNode.child("triceps");
                break;
            }
            case "chest": {
                myRef = exercisesNode.child("chest");
                break;
            }
            case "shoulders": {
                myRef = exercisesNode.child("shoulders");
                break;
            }
            case "biceps": {
                myRef = exercisesNode.child("biceps");
                break;
            }
            case "abs": {
                myRef = exercisesNode.child("abs");
                break;
            }
            case "back": {
                myRef = exercisesNode.child("back");
                break;
            }
            case "cardio": {
                myRef = exercisesNode.child("cardio");
                break;
            }
            case "leg": {
                myRef = exercisesNode.child("leg");
                break;
            }
            case "showall": {
                myRef = exercisesNode;
                loadAllExercises(myRef);
                break;
            }


        }

        /** code inside if should only execute if selected muscle is not "showall"*/
        if (!myRef.equals(exercisesNode)) {
            //once we selected the right muscle group node this could will be the same for all exercises info
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //TODO only show in main list the exercises that admin added(remove comment)
                        /*  if (ds.child("creatorId").getValue().equals(MyConstant.AdminId)) {*/
                            Exercise exercise = new Exercise();
                            exercise.setName(ds.child("name").getValue().toString());
                            exercise.setExecution(ds.child("execution").getValue().toString());
                        if (ds.hasChild("additional_notes"))
                            exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                            exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            exerciseList.add(exercise);
                        }
                    /* }*/

                    load.setValue(exerciseList);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        return load;
    }

    private void loadAllExercises(DatabaseReference myRef) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsBig : dataSnapshot.getChildren()) {
                    for (DataSnapshot ds : dsBig.getChildren()) {
                        //TODO only show in main list the exercises that admin added(remove comment)
                        /*  if (ds.child("creatorId").getValue().equals(MyConstant.AdminId)) {*/
                            Exercise exercise = new Exercise();
                            exercise.setName(ds.child("name").getValue().toString());
                            exercise.setExecution(ds.child("execution").getValue().toString());
                        if (ds.hasChild("additional_notes"))
                            exercise.setAdditional_notes(ds.child("additional_notes").getValue().toString());
                            exercise.setBodyPart(ds.child("bodyPart").getValue().toString());
                            exercise.setMechanism(ds.child("mechanism").getValue().toString());
                            exercise.setPreviewPhoto1(ds.child("previewPhoto1").getValue().toString());
                            exercise.setPreviewPhoto2(ds.child("previewPhoto2").getValue().toString());
                            exerciseList.add(exercise);
                        }
                    /* }*/
                }
                load.setValue(exerciseList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public LiveData<List<Exercise>> downloadExercisesImages(final List<Exercise> exerciseList) {

        /**if exerciseList is the same list i got from getExercise method no need to download images since it is the same*/
        if (exerciseList.equals(this.exerciseList) && load2 != null) {
            // load.setValue(exerciseList);
            return load2;
        }

        load2 = new MutableLiveData<>();

        //download images and store them as bitmap in the model class so later we can show them in the adapter
        for (int i = 0; i < exerciseList.size(); i++) {
            //download preview image 1
            //todo find better way to download images(glide)
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("exerciseImages/").child(exerciseList.get(i).getPreviewPhoto1());
            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
                final int finalI = i;
                final File finalLocalFile = localFile;
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        exerciseList.get(finalI).setPreviewBitmap(BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        load2.setValue(exerciseList);

        return load2;

    }


}
