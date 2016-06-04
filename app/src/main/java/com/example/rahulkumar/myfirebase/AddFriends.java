package com.example.rahulkumar.myfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddFriends extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.rv_friends)
    RecyclerView rv_friends;

    @Bind(R.id.search_view)
    MaterialSearchView searchView;

    @Bind(R.id.progress_bar_users)
    ProgressBar progress_bar_users;

    @Bind(R.id.txt_noresult)
    AppCompatTextView txt_noresult;
    Firebase myConnectionsStatusRef;
    SharedPreferences prefs;
    String id;
    private Firebase firebaseRootRef, fireBaseUsers, firebasefriends;
    private Firebase.AuthStateListener mAuthStateListener;
    private AuthData mAuthData;
    private String mCurrentUserId;
    private String mCurrentUserEmail;
    private ChildEventListener mListenerUsers;
    private List<String> mUsersKeyList;
    private ValueEventListener mConnectedListener;
    private List<UsersChatModel> usersChatModels = new ArrayList<>();
    private List<FriendsRequestsModel> friendsRequestsModels = new ArrayList<>();
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        mUsersKeyList = new ArrayList<>();

        searchAdapter = new SearchAdapter(usersChatModels);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setHasFixedSize(true);
        rv_friends.setAdapter(searchAdapter);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                List<UsersChatModel> list = new ArrayList<UsersChatModel>();
                if (newText.trim().length() > 0) {
                    if (usersChatModels.size() > 0) {
                        for (UsersChatModel model : usersChatModels) {
                            if (model.getFirstName().contains(newText)) {
                                list.add(model);
                            }
                        }
                    }
                    if (list.size() > 0) {
                        searchAdapter.Notify(list);
                    }
                } else {
                    searchAdapter.Notify(usersChatModels);
                }
                return false;
            }
        });
        prefs = getSharedPreferences("MyApp", 0);
        id = prefs.getString("id", "");
        firebaseRootRef = MySingleton.getInstanceUsingDoubleLocking();
        fireBaseUsers = firebaseRootRef.child(ReferenceUrl.CHILD_USERS);
        firebasefriends = firebaseRootRef.child(ReferenceUrl.CHILD_FRIENDS);
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                SetAuthentication(authData);
            }
        };
        firebaseRootRef.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            logout();
        }

    }

    private void SetAuthentication(AuthData authData) {
        mAuthData = authData;
        if (mAuthData == null) {
            Utils.Logout(AddFriends.this);
        } else {
            mCurrentUserId = mAuthData.getUid();
            mCurrentUserEmail = (String) mAuthData.getProviderData().get(ReferenceUrl.KEY_EMAIL);
            FindUsers();
        }
    }

    private void ShowProgressBarForUsers() {
        progress_bar_users.setVisibility(View.VISIBLE);
    }

    private void hideProgressBarForUsers() {
        if (progress_bar_users.getVisibility() == View.VISIBLE) {
            progress_bar_users.setVisibility(View.GONE);
        }
    }

    private void FindUsers() {
        ShowProgressBarForUsers();
        usersChatModels.clear();
        final List<UsersChatModel> list = new ArrayList<>();
        mListenerUsers = fireBaseUsers.limitToFirst(50).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                hideProgressBarForUsers();

                if (dataSnapshot.exists()) {
                    String userUid = dataSnapshot.getKey();
                    if (!userUid.equals(mCurrentUserId)) {
                        UsersChatModel usersChatModel = dataSnapshot.getValue(UsersChatModel.class);
                        System.out.println("count is" + dataSnapshot.getChildrenCount());
                        System.out.println("email" + usersChatModel.getUserEmail());

                        if (usersChatModel.getUserEmail() != null) {
                            usersChatModel.setRecipientUid(dataSnapshot.getKey());
                            System.out.println("id is" + usersChatModel.getRecipientUid());
                            System.out.println("friendlist size" + friendsRequestsModels.size());
                            if (friendsRequestsModels.size() > 0) {
                                //list.add(usersChatModel);
                                List<String> ownerid = new ArrayList<String>();
                                List<String> rec_id = new ArrayList<String>();
                                for (FriendsRequestsModel model : friendsRequestsModels) {
                                    ownerid.add(model.getOwnerid());
                                    rec_id.add(model.getRecipientid());
                                }
                                if (ownerid.contains(usersChatModel.getRecipientUid()) || rec_id.contains(usersChatModel.getRecipientUid()) || ownerid.contains(userUid) || rec_id.contains(userUid)) {

                                } else {
                                    usersChatModels.add(usersChatModel);
                                    Set<UsersChatModel> set = new HashSet<UsersChatModel>(usersChatModels);
                                    usersChatModels = new ArrayList<UsersChatModel>(set);
                                }
                            } else {
                                usersChatModels.add(usersChatModel);
                                Set<UsersChatModel> set = new HashSet<UsersChatModel>(usersChatModels);
                                usersChatModels = new ArrayList<UsersChatModel>(set);
                            }
                            if (usersChatModels.size() > 0) {
                                txt_noresult.setVisibility(View.GONE);
                                rv_friends.setVisibility(View.VISIBLE);
                                searchAdapter.Notify(usersChatModels);
                            } else {
                                rv_friends.setVisibility(View.GONE);
                                txt_noresult.setVisibility(View.VISIBLE);
                            }


                        }

                    } else {
                        UsersChatModel usersChatModel = dataSnapshot.getValue(UsersChatModel.class);
                        searchAdapter.SetFirstLastName(usersChatModel.getFirstName(), usersChatModel.getLastName());
                    }
                }
                System.out.println("inside size");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("child changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("child moved");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("cancel");
            }
        });

        firebasefriends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    FriendsRequestsModel model = dataSnapshot.getValue(FriendsRequestsModel.class);
                    friendsRequestsModels.add(model);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        myConnectionsStatusRef = fireBaseUsers.child(mCurrentUserId).child(ReferenceUrl.CHILD_CONNECTION);

        mConnectedListener = firebaseRootRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {

                    myConnectionsStatusRef.setValue(ReferenceUrl.KEY_ONLINE);

                    // When this device disconnects, remove it
                    myConnectionsStatusRef.onDisconnect().setValue(ReferenceUrl.KEY_OFFLINE);
                    Toast.makeText(AddFriends.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(AddFriends.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        System.out.println("iiiiinnnnnnnnn");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fireBaseUsers.removeAuthStateListener(mAuthStateListener);

        mUsersKeyList.clear();

        // Stop all listeners
        // Make sure to check if they have been initialized
        if (mListenerUsers != null) {
            fireBaseUsers.removeEventListener(mListenerUsers);
        }
    }

    private void logout() {

        if (this.mAuthData != null) {

            /* Logout of mChat */

            // Store current user status as offline
            myConnectionsStatusRef.setValue(ReferenceUrl.KEY_OFFLINE);

            // Finish token
            fireBaseUsers.unauth();

            /* Update authenticated user and show login screen */
            //SetAuthentication(null);
        }
    }

}
