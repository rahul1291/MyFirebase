package com.example.rahulkumar.myfirebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by rahulkumar on 23/05/16.
 */
public class Utils {

    public static void HideKeyboard(Activity activity) {
        @SuppressWarnings("static-access")
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getApplicationWindowToken(), 0);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    }

    public static void Logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyApp", 0);
        prefs.edit().clear().commit();
        Intent logout = new Intent(context
                , MainActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(logout);
    }
}
