package ganainy.dev.gymmasters.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public class FirebaseUtils {

    @NonNull
    public static Workout getWorkoutFromSnapshot(DataSnapshot ds) {
        Workout workout = new Workout();
        if (ds.hasChild("name"))
            workout.setName(ds.child("name").getValue().toString());
        if (ds.hasChild("duration"))
            workout.setDuration(ds.child("duration").getValue().toString() + " mins");
        if (ds.hasChild("exercisesNumber"))
            workout.setExercisesNumber(ds.child("exercisesNumber").getValue().toString());
        if (ds.hasChild("photoLink"))
            workout.setPhotoLink(ds.child("photoLink").getValue().toString());
        if (ds.hasChild("id"))
            workout.setId(ds.child("id").getValue().toString());
        if (ds.hasChild("level"))
            workout.setLevel(ds.child("level").getValue().toString());
        return workout;
    }

    public static Exercise getExerciseFromSnapshot(DataSnapshot snapshot) {
        Exercise exercise = new Exercise();
        if (snapshot.hasChild("name"))
            exercise.setName(snapshot.child("name").getValue().toString());
        if (snapshot.hasChild("execution"))
            exercise.setExecution(snapshot.child("execution").getValue().toString());
        if (snapshot.hasChild("additional_notes"))
            exercise.setAdditional_notes(snapshot.child("additional_notes").getValue().toString());
        if (snapshot.hasChild("bodyPart"))
            exercise.setBodyPart(snapshot.child("bodyPart").getValue().toString());
        if (snapshot.hasChild("mechanism"))
            exercise.setMechanism(snapshot.child("mechanism").getValue().toString());
        if (snapshot.hasChild("previewPhoto1"))
            exercise.setPreviewPhoto1(snapshot.child("previewPhoto1").getValue().toString());
        if (snapshot.hasChild("previewPhoto2"))
            exercise.setPreviewPhoto2(snapshot.child("previewPhoto2").getValue().toString());
        return exercise;
    }


    public static User getUserFromSnapshot(@NonNull DataSnapshot dataSnapshot) {
        User user = new User();
        String name = dataSnapshot.child("name").getValue().toString();
        user.setName(name);
        String email = dataSnapshot.child("email").getValue().toString();
        user.setEmail(email);
        if (dataSnapshot.hasChild("photo")) {
            String photo = dataSnapshot.child("photo").getValue().toString();
            user.setPhoto(photo);
        }
        if (dataSnapshot.hasChild("about_me")) {
            String about_me = dataSnapshot.child("about_me").getValue().toString();
            user.setAbout_me(about_me);
        }
        return user;
    }

}
