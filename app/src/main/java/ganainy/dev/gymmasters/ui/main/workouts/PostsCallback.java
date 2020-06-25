package ganainy.dev.gymmasters.ui.main.workouts;

import ganainy.dev.gymmasters.models.app_models.Post;

public interface PostsCallback {
    void onOpenFindUsersActivity(String key,String value);
    void onOpenPostCommentFragment(Post post);
}
