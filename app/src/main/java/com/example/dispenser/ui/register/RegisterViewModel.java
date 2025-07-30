package com.example.dispenser.ui.register;

import android.content.Intent;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dispenser.R;
import com.example.dispenser.data.AuthRepositoryImpl;
import com.example.dispenser.data.Result;
import com.example.dispenser.data.model.User;
import com.example.dispenser.ui.FormState;
import com.example.dispenser.ui.login.LoginActivity;


public class RegisterViewModel extends ViewModel {


    private boolean isRegistering = false;
    private MutableLiveData<FormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private AuthRepositoryImpl authRepositoryImpl;

    RegisterViewModel(AuthRepositoryImpl authRepositoryImpl) {
        this.authRepositoryImpl = authRepositoryImpl;
    }
    public boolean isRegistering() {
        return isRegistering;
    }
    LiveData<FormState> getregisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getregisterResult() {
        return registerResult;
    }
    public boolean registerBoolean(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<User> result = authRepositoryImpl.register(username, password);

        if (result instanceof Result.Success) {
            User data = ((Result.Success<User>) result).getData();
            isRegistering = true;
            registerResult.setValue(new RegisterResult(new RegistedInUserView(data.getDisplayName())));
        } else {
            isRegistering=false;
            registerResult.setValue(new RegisterResult(R.string.register_failed));
        }
        return isRegistering;
    }
    public void register(String username, String password) {
        // can be launched in a separate asynchronous job
        Result<User> result = authRepositoryImpl.register(username, password);

        if (result instanceof Result.Success) {
            User data = ((Result.Success<User>) result).getData();
            isRegistering = true;
            registerResult.setValue(new RegisterResult(new RegistedInUserView(data.getDisplayName())));
        } else {
            isRegistering=false;
            registerResult.setValue(new RegisterResult(R.string.register_failed));
        }
    }

    public void registerDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new FormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new FormState(null, R.string.invalid_password));
        } else registerFormState.setValue(new FormState(true));
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
