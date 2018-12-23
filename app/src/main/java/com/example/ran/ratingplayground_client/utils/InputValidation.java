package com.example.ran.ratingplayground_client.utils;

import android.util.Patterns;

public class InputValidation {

    public static boolean validateUserInput(String input) {
        if (input.isEmpty()) {
            return false;
        }
        return true;
    }


    public static boolean validateEmailAddress(String email) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        return true;
    }

}
