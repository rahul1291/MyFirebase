package com.example.rahulkumar.myfirebase;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by rahulkumar on 20/05/16.
 */
public class MyProgressDialog {

    static ProgressDialog progressDialog;

    public static void ShowDialog(Context context) {
        progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT);
        Dismiss();
        if (!progressDialog.isShowing()) {
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public static void Dismiss() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
