package com.example.dispenser.ui.register;

public class RegistedInUserView {
    private String displayName;
    //... other data fields that may be accessible to the UI

    RegistedInUserView(String displayName) {
        this.displayName = displayName;
    }

    String getDisplayName() {
        return displayName;
    }
}
