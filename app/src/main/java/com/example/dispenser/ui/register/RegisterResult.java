package com.example.dispenser.ui.register;

import androidx.annotation.Nullable;


public class RegisterResult {
    @Nullable
    private RegistedInUserView success;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable RegistedInUserView success) {
        this.success = success;
    }

    @Nullable
    RegistedInUserView getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}
