package com.example.rahulkumar.myfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FriendsActivity extends AppCompatActivity {

    Firebase firebase;
    @Bind(R.id.rv_friends)
    RecyclerView rv_friends;
    @Bind(R.id.txtNoFriends)
    AppCompatTextView txtNoFriends;
    @Bind(R.id.btnAdd)
    FloatingActionButton btnAdd;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    List<FriendsRequestsModel> friendsRequestsModelList = new ArrayList<>();
    String id;
    SharedPreferences prefs;
    MyFriendsAdapter adapter;
    private Firebase.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        ButterKnife.bind(this);
        adapter = new MyFriendsAdapter(friendsRequestsModelList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv_friends.setLayoutManager(new LinearLayoutManager(this));
        rv_friends.setHasFixedSize(true);
        rv_friends.setAdapter(adapter);

        firebase = MySingleton.getInstanceUsingDoubleLocking();

        prefs = getSharedPreferences("MyApp", 0);
        id = prefs.getString("id", "");
        FindFriends();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friendsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Utils.Logout(FriendsActivity.this);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.friend_request:
                Intent friends_to_friend_req = new Intent(FriendsActivity.this, FriendRequests.class);
                friends_to_friend_req.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(friends_to_friend_req);
                break;
        }
        return true;
    }

    @OnClick(R.id.btnAdd)
    public void Search() {
        Intent friends_to_search = new Intent(FriendsActivity.this, AddFriends.class);
        friends_to_search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(friends_to_search);
    }


    public void FindFriends() {

        MyProgressDialog.ShowDialog(FriendsActivity.this);
        firebase = firebase.child(ReferenceUrl.CHILD_FRIENDS);
        friendsRequestsModelList.clear();
        firebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MyProgressDialog.Dismiss();
                if (dataSnapshot.exists()) {

                    FriendsRequestsModel model = dataSnapshot.getValue(FriendsRequestsModel.class);
                    System.out.println("id is" + id);
                    System.out.println("owner id" + model.getOwnerid());
                    System.out.println("recipient id" + model.getRecipientid());
                    if ((id.equalsIgnoreCase(model.getOwnerid()) || id.equalsIgnoreCase(model.getRecipientid())) && model.getStatus().equalsIgnoreCase("accepted")) {
                        friendsRequestsModelList.add(model);
                    }

                    if (friendsRequestsModelList.size() > 0) {
                        rv_friends.setVisibility(View.VISIBLE);
                        txtNoFriends.setVisibility(View.GONE);
                        adapter.Notify(friendsRequestsModelList);

                    } else {
                        rv_friends.setVisibility(View.GONE);
                        txtNoFriends.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                System.out.println("changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                System.out.println("removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                System.out.println("moved");
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("cancelled");
            }
        });

    }
}
