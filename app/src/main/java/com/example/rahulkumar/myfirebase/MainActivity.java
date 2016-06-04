package com.example.rahulkumar.myfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Firebase firebase;

    @Bind(R.id.txtEmailAddress)
    EditText txtEmailAddress;

    @Bind(R.id.txtPassword)
    EditText txtPassword;

    @Bind(R.id.btnLogin)
    AppCompatButton btnLogin;

    @Bind(R.id.btnRegister)
    AppCompatButton btnRegister;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("MyApp", 0);
        if (prefs.getString("id", "") != null && !prefs.getString("id", "").equalsIgnoreCase("")) {
            Intent main_to_users = new Intent(MainActivity.this, FriendsActivity.class);
            main_to_users.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main_to_users);
        } else {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            firebase = MySingleton.getInstanceUsingDoubleLocking();
            btnLogin.setOnClickListener(this);
            btnRegister.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                Utils.HideKeyboard(MainActivity.this);
                if (!Patterns.EMAIL_ADDRESS.matcher(txtEmailAddress.getText().toString().trim()).matches()) {
                    Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.getText().toString().trim().length() == 0) {
                    Toast.makeText(MainActivity.this, "Inavlid password", Toast.LENGTH_SHORT).show();
                } else {
                    MyProgressDialog.ShowDialog(MainActivity.this);
                    firebase.authWithPassword(txtEmailAddress.getText().toString().trim(), txtPassword.getText().toString().trim(), new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {

                            MyProgressDialog.Dismiss();
                            prefs = getSharedPreferences("MyApp", 0);
                            prefs.edit().putString("id", authData.getUid()).commit();
                            finish();
                            Toast.makeText(MainActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            Intent main_to_users = new Intent(MainActivity.this, FriendsActivity.class);
                            main_to_users.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main_to_users);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            MyProgressDialog.Dismiss();
                            Toast.makeText(MainActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                break;
            case R.id.btnRegister:
                Intent main_to_register = new Intent(MainActivity.this, RegisterActivity.class);
                main_to_register.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main_to_register);
                break;
        }
    }
}
