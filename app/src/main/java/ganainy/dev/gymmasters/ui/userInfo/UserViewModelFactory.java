package ganainy.dev.gymmasters.ui.userInfo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.InvocationTargetException;

import ganainy.dev.gymmasters.models.app_models.User;

public class UserViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private User user;

    public UserViewModelFactory(Application application, User user) {
        this.application = application;
        this.user=user;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        try {
            return modelClass.getConstructor(Application.class,User.class).newInstance(application,user);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

