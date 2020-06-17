package ganainy.dev.gymmasters.ui.findUser;

import ganainy.dev.gymmasters.models.app_models.User;

public interface UserCallback {
    void onUserClicked(User user, Integer adapterPosition);
}
