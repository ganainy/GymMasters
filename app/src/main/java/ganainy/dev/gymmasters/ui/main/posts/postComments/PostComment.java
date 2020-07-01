package ganainy.dev.gymmasters.ui.main.posts.postComments;

import androidx.core.util.Pair;

import ganainy.dev.gymmasters.models.app_models.Comment;
import ganainy.dev.gymmasters.models.app_models.Post;
import ganainy.dev.gymmasters.models.app_models.User;

import static ganainy.dev.gymmasters.ui.main.posts.postComments.PostComment.PostCommentType.COMMENT;
import static ganainy.dev.gymmasters.ui.main.posts.postComments.PostComment.PostCommentType.POST_EXERCISE;
import static ganainy.dev.gymmasters.ui.main.posts.postComments.PostComment.PostCommentType.POST_WORKOUT;

public class PostComment {

    public Pair<Comment, User> getUserCommentPair() {
        return userCommentPair;
    }

    public void setUserCommentPairList(Pair<Comment, User> userCommentPair) {
        this.userCommentPair = userCommentPair;
    }

    private PostCommentType postCommentType;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

     private Pair<Comment, User> userCommentPair;
     private Post post;

    public PostCommentType getPostCommentType() {
       return this.postCommentType;
    }


    public PostComment(Pair<Comment, User> userCommentPair) {
        this.userCommentPair = userCommentPair;
        this.postCommentType=COMMENT;
    }

    public PostComment( Post post) {
        this.post = post;
        if (post.getExercise()!=null){
            this.postCommentType=POST_EXERCISE;
        }else
            this.postCommentType=POST_WORKOUT;
    }

    public PostComment(PostCommentType postCommentType) {
        this.postCommentType=postCommentType;
    }

    public enum PostCommentType{
        POST_EXERCISE,POST_WORKOUT,COMMENT,LOADING_COMMENTS,EMPTY_COMMENTS
    }
}
