package com.example.rahulkumar.myfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.txtFname)
    EditText txtFname;

    @Bind(R.id.txtLname)
    EditText txtLname;

    @Bind(R.id.txtEmailAddress)
    EditText txtEmailAddress;

    @Bind(R.id.txtPassword)
    EditText txtPassword;

    @Bind(R.id.btnRegister)
    AppCompatButton btnRegister;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebase = MySingleton.getInstanceUsingDoubleLocking();
    }

    @OnClick(R.id.btnRegister)
    public void Register() {
        Utils.HideKeyboard(RegisterActivity.this);
        if (txtFname.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Invalid first name", Toast.LENGTH_SHORT).show();
        } else if (txtLname.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Invalid last name", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(txtEmailAddress.getText().toString().trim()).matches()) {
            Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
        } else if (txtPassword.getText().toString().trim().length() < 6) {
            Toast.makeText(RegisterActivity.this, "Inavlid password", Toast.LENGTH_SHORT).show();
        } else {
            MyProgressDialog.ShowDialog(RegisterActivity.this);
            firebase.createUser(txtEmailAddress.getText().toString().trim(), txtPassword.getText().toString().trim(), new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    MyProgressDialog.Dismiss();
                    MyProgressDialog.ShowDialog(RegisterActivity.this);
                    Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                    firebase.authWithPassword(txtEmailAddress.getText().toString().trim(), txtPassword.getText().toString().trim(), new Firebase.AuthResultHandler() {
                        @Override
                        public void onAuthenticated(AuthData authData) {
                            MyProgressDialog.Dismiss();

                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put(ReferenceUrl.KEY_PROVIDER, authData.getProvider()); // The authentication method used
                            map.put(ReferenceUrl.KEY_FIRST_NAME, txtFname.getText().toString().trim());   // User first name
                            map.put(ReferenceUrl.KEY_LAST_NAME, txtLname.getText().toString().trim());
                            map.put(ReferenceUrl.KEY_USER_EMAIL, (String) authData.getProviderData().get(ReferenceUrl.KEY_EMAIL)); // User email address
                            map.put(ReferenceUrl.CHILD_CONNECTION, ReferenceUrl.KEY_ONLINE);  // User status

                            // Time user date is stored in database
                            long createTime = new Date().getTime();
                            map.put(ReferenceUrl.KEY_TIMESTAMP, String.valueOf(createTime)); // Timestamp is string type
                            firebase.child(ReferenceUrl.CHILD_USERS).child(authData.getUid()).setValue(map);

                            SharedPreferences prefs = getSharedPreferences("MyApp", 0);
                            prefs.edit().putString("id", authData.getUid()).commit();

                            finish();
                            Intent intent = new Intent(RegisterActivity.this, FriendsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onAuthenticationError(FirebaseError firebaseError) {
                            MyProgressDialog.Dismiss();
                        }
                    });
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    MyProgressDialog.Dismiss();
                    Toast.makeText(RegisterActivity.this, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
