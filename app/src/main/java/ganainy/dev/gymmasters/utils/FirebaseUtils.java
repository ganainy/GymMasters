package ganainy.dev.gymmasters.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;

public class FirebaseUtils {
//todo delete this and replace with getvalue(class type)
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
        if (ds.hasChild("date"))
            workout.setDate(ds.child("date").getValue().toString());
        if (ds.hasChild("creatorId"))
            workout.setCreatorId(ds.child("creatorId").getValue().toString());
        return workout;
    }

    public static Exercise getExerciseFromSnapshot(DataSnapshot snapshot) {
        Exercise exercise = new Exercise();
        if (snapshot.hasChild("id"))
            exercise.setId(snapshot.child("id").getValue().toString());
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
        if (snapshot.hasChild("previewPhotoOneUrl"))
            exercise.setPreviewPhotoOneUrl(snapshot.child("previewPhotoOneUrl").getValue().toString());
        if (snapshot.hasChild("previewPhotoTwoUrl"))
            exercise.setPreviewPhotoTwoUrl(snapshot.child("previewPhotoTwoUrl").getValue().toString());
        if (snapshot.hasChild("creatorId"))
            exercise.setCreatorId(snapshot.child("creatorId").getValue().toString());
        if (snapshot.hasChild("date"))
            exercise.setDate(snapshot.child("date").getValue().toString());
        return exercise;
    }


    public static User getUserFromSnapshot(@NonNull DataSnapshot dataSnapshot) {
        User user = new User();
        if (dataSnapshot.hasChild("name")) {
            String name = dataSnapshot.child("name").getValue().toString();
            user.setName(name);
        }
        if (dataSnapshot.hasChild("email")) {
            String email = dataSnapshot.child("email").getValue().toString();
            user.setEmail(email);
        }
        if (dataSnapshot.hasChild("photo")) {
            String photo = dataSnapshot.child("photo").getValue().toString();
            user.setPhoto(photo);
        }
        if (dataSnapshot.hasChild("about_me")) {
            String about_me = dataSnapshot.child("about_me").getValue().toString();
            user.setAbout_me(about_me);
        }
        if (dataSnapshot.hasChild("id")) {
            String id = dataSnapshot.child("id").getValue().toString();
            user.setId(id);
        }
        return user;
    }

}
