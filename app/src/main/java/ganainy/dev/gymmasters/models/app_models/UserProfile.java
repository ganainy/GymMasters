package ganainy.dev.gymmasters.models.app_models;

import java.util.List;

import ganainy.dev.gymmasters.models.app_models.Exercise;
import ganainy.dev.gymmasters.models.app_models.User;
import ganainy.dev.gymmasters.models.app_models.Workout;
import ganainy.dev.gymmasters.ui.userInfo.FollowState;

public class UserProfile {

    private FollowState followState;
    private Long ratingAverage;
    private Long followersCount;

    public User getProfileOwner() {
        return profileOwner;
    }

    public void setProfileOwner(User profileOwner) {
        this.profileOwner = profileOwner;
    }

    private User profileOwner;

    public Long getLoggedUserRating() {
        return loggedUserRating;
    }

    public void setLoggedUserRating(Long loggedUserRating) {
        this.loggedUserRating = loggedUserRating;
    }

    private Long loggedUserRating;

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    private Long followingCount;

    public FollowState getFollowState() {
        return followState;
    }

    public void setFollowState(FollowState followState) {
        this.followState = followState;
    }

    public List<Exercise> getExercisesList() {
        return exercisesList;
    }

    public void setExercisesList(List<Exercise> exercisesList) {
        this.exercisesList = exercisesList;
    }

    private List<Exercise> exercisesList;

    public List<Workout> getWorkoutList() {
        return workoutList;
    }

    public void setWorkoutList(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    private List<Workout> workoutList;

    public UserProfile() {
    }


    public Long getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(Long ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }
}
